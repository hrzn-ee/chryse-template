#include <csignal>
#include <fstream>
#include <iostream>

#include <cxxrtl/cxxrtl_vcd.h>
#include <newproject.h>

int main(int argc, char **argv) {
  cxxrtl_design::p_top top;
  debug_items di;
  top.debug_info(&di, nullptr, "top ");

  bool do_vcd = argc >= 2 && std::string(argv[1]) == "--vcd";
  cxxrtl::vcd_writer vcd;
  uint64_t vcd_time = 0;
  if (do_vcd)
    vcd.add(di);
  top.p_reset.set(true);
  top.p_clock.set(true);
  top.step();
  vcd.sample(vcd_time++);
  top.p_clock.set(false);
  top.step();
  vcd.sample(vcd_time++);
  top.p_reset.set(false);

  std::cout << "finished on cycle " << (vcd_time >> 1) << std::endl;

  if (do_vcd) {
    std::ofstream of("cxxsim.vcd");
    of << vcd.buffer;
  }

  return 0;
}