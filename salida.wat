(module
  (import "env" "log" (func $log (param i32)))
  (import "env" "logf" (func $logf (param f64)))
  (import "env" "logs" (func $logs (param i32 i32)))
  (import "env" "abort" (func $abort (param i32)))
  (memory (export "memory") 1)
  (global $g_cero_main (mut f64) (f64.const 0.0))
  (global $g_medio_1_main (mut f64) (f64.const 0.0))
  (global $g_medio_2_main (mut f64) (f64.const 0.0))
  (global $g_medio_3_main (mut f64) (f64.const 0.0))
  (global $g_medio_4_main (mut f64) (f64.const 0.0))
  (global $g_medio_5_main (mut f64) (f64.const 0.0))
  (global $g_medio_6_main (mut f64) (f64.const 0.0))
  (global $g_medio_7_main (mut f64) (f64.const 0.0))
  (func $__main (export "main") (result i32)
    (local $tmp0 f64)
    (local $tmp1 f64)
    (local $tmp2 f64)
    (local $tmp3 f64)
    (local $tmp4 f64)
    (local $tmp5 f64)
  f64.const 1.3
  local.set $tmp0
  local.get $tmp0
  global.set $g_medio_1_main
  f64.const 0.2
  local.set $tmp1
  local.get $tmp1
  global.set $g_medio_2_main
  f64.const 2.0e-6
  local.set $tmp2
  local.get $tmp2
  global.set $g_medio_3_main
  f64.const 1.2e10
  local.set $tmp3
  local.get $tmp3
  global.set $g_medio_4_main
  f64.const -1.52e-4
  local.set $tmp4
  local.get $tmp4
  global.set $g_medio_5_main
  f64.const 0.0
  local.set $tmp5
  local.get $tmp5
  global.set $g_cero_main
  i32.const 0
  )
)
