package org.lodsb.phantasmatron.core

import eu.mihosoft.vrl.workflow.{VNode, Connector}
import org.lodsb.reakt.{TVar, TSignal}

/**
 * Created by lodsb on 12/21/13.
 */
object ConnectionManager {

  private var nodeMap = Map[VNode, List[ConnectorDescriptor[_]]]()
  private var connectionMap = Map[VNode, List[(ConnectorDescriptor[_], ConnectorDescriptor[_])]]()
  private var connectorMap = Map[Connector, ConnectorDescriptor[_]]()

  def addConnectorDescr(desc: ConnectorDescriptor[_]) = {
    synchronized {
      val lookup = nodeMap.get(desc.node)

      var list = List[ConnectorDescriptor[_]]()

      if (!lookup.isEmpty) {
        list = lookup.get
      }

      list = list :+ desc

      nodeMap = nodeMap + (desc.node -> list)

      connectorMap = connectorMap + (desc.connector -> desc)
    }
  }

  def diconnectAll(vnode: VNode) = {
    synchronized {
      val lookup = connectionMap.get(vnode)

      if (lookup.isDefined) {
        lookup.get.foreach {
          x =>
            x._1.signal.signal.disconnect(x._2.signal.signal)
        }
      }
    }
  }

  def connect(src: Connector, dst: Connector) = {
    synchronized {
      val srcDescL = connectorMap.get(src)
      val dstDescL = connectorMap.get(dst)

      if(srcDescL.isDefined && dstDescL.isDefined) {
        val srcDesc = srcDescL.get
        val dstDesc = dstDescL.get

        //if(srcDesc.signal.outerTypeTag.tpe.<:<(dstDesc.signal.outerTypeTag.tpe)) {

          val l = connectionMap.get(srcDesc.node)
          var list = l.getOrElse(List[(ConnectorDescriptor[_], ConnectorDescriptor[_])]())

          (dstDesc.signal.signal.asInstanceOf[TVar[Any]]) <~ srcDesc.signal.signal.asInstanceOf[TVar[Any]]

          list = list :+ (srcDesc, dstDesc)

          connectionMap = connectionMap + (srcDesc.node -> list)

        //}




        //srcDesc.signal.signal ~> dstDesc.signal.signal
      }

    }
  }

  def getDescriptor(connector: Connector) : Option[ConnectorDescriptor[_]]= {
    connectorMap.get(connector)
  }

  def disconnect(src: Connector, dst: Connector) = {
    synchronized {
      val srcDescL = connectorMap.get(src)
      val dstDescL = connectorMap.get(dst)

      if(srcDescL.isDefined && dstDescL.isDefined) {
        val srcDesc = srcDescL.get
        val dstDesc = dstDescL.get

        if(srcDesc.signal.outerTypeTag.tpe.<:<(dstDesc.signal.outerTypeTag.tpe)) {
          val l = connectionMap.get(srcDesc.node)
          var list = l.getOrElse(List[(ConnectorDescriptor[_], ConnectorDescriptor[_])]())

          srcDesc.signal.signal.asInstanceOf[TVar[Any]].disconnect(dstDesc.signal.signal.asInstanceOf[TVar[Any]])
          dstDesc.signal.signal.asInstanceOf[TVar[Any]].disconnect(srcDesc.signal.signal.asInstanceOf[TVar[Any]])

          println("disconnect")

          list = list diff List((srcDesc, dstDesc))

          connectionMap = connectionMap + (srcDesc.node -> list)

        }
      }

    }
  }

}
