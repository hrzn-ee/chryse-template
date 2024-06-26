package com.example.newproject

import chisel3._
import chisel3.util._
import ee.hrzn.chryse.platform.Platform
import ee.hrzn.chryse.platform.cxxrtl.CxxrtlPlatform
import ee.hrzn.chryse.platform.ecp5.Ulx3SPlatform
import ee.hrzn.chryse.platform.ice40.IceBreakerPlatform

class Top(implicit platform: Platform) extends Module {
  override def desiredName = "newproject"

  val blinker = Module(new Blinker)

  platform match {
    case plat: IceBreakerPlatform =>
      plat.resources.ledr := blinker.io.ledr
      plat.resources.ledg := blinker.io.ledg

    case plat: Ulx3SPlatform =>
      plat.resources.leds(0) := blinker.io.ledr
      plat.resources.leds(1) := blinker.io.ledg

    case plat: CxxrtlPlatform =>
      val io = IO(new BlinkerIO)
      io :<>= blinker.io

    case _ =>
  }
}

class BlinkerIO extends Bundle {
  val ledr = Output(Bool())
  val ledg = Output(Bool())
}

class Blinker(implicit platform: Platform) extends Module {
  val io = IO(new BlinkerIO)

  val ledrReg = RegInit(false.B)
  io.ledr := ledrReg
  io.ledg := true.B

  val timerReg = RegInit(
    ((platform.clockHz / 4) - 1)
      .U(unsignedBitLength((platform.clockHz / 2) - 1).W),
  )
  when(timerReg === 0.U) {
    ledrReg  := ~ledrReg
    timerReg := ((platform.clockHz / 2) - 1).U
  }.otherwise {
    timerReg := timerReg - 1.U
  }
}
