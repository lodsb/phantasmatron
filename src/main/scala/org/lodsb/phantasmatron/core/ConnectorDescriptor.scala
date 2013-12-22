package org.lodsb.phantasmatron.core

import eu.mihosoft.vrl.workflow.{Connector, VNode}
import org.lodsb.reakt.{TVar, TSignal}

case class ConnectorDescriptor[X](node: VNode, connector: Connector, signal: TaggedSignal[X,_ <: TVar[X]])
