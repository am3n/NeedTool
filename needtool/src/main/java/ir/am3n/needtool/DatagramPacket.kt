package ir.am3n.needtool

import java.net.DatagramPacket

val DatagramPacket.str: String get() = String(this.data, 0, this.length)