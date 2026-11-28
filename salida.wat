(module
  (import "env" "log" (func $log (param i32)))
  (import "env" "logf" (func $logf (param f64)))
  (import "env" "logs" (func $logs (param i32 i32)))
  (import "env" "abort" (func $abort (param i32)))
  (memory (export "memory") 1)
  (global $g_a_main (mut f64) (f64.const 0.0))
  (global $g_b_main (mut f64) (f64.const 0.0))
  (global $g_c_main (mut f64) (f64.const 0.0))
  (global $g_resultado_main (mut f64) (f64.const 0.0))
  (func $fn_sumar_tres_main (param $l_p1_main_sumar_tres f64) (param $l_p2_main_sumar_tres f64) (param $l_p3_main_sumar_tres f64) (result f64)
    (local $tmp0 f64)
  local.get $l_p1_main_sumar_tres
  local.get $l_p2_main_sumar_tres
  f64.sub
  local.get $l_p3_main_sumar_tres
  f64.add
  local.set $tmp0
  local.get $tmp0
  f64.abs
  f64.const 1.7976931348623157e308
  f64.gt
  if
    i32.const 3
  call $abort
  unreachable
  end
  local.get $tmp0
  return
  f64.const 0.0
  return
  )
  (func $__main (export "main") (result i32)
    (local $tmp0 f64)
    (local $tmp1 f64)
    (local $tmp2 f64)
    (local $tmp3 f64)
    (local $tmp4 f64)
    (local $tmp5 f64)
    (local $tmp6 f64)
    (local $tmp7 f64)
    (local $tmp8 f64)
    (local $tmp9 f64)
    (local $tmp10 f64)
    (local $tmp11 f64)
    (local $tmp12 f64)
    (local $tmp13 f64)
    (local $tmp14 f64)
    (local $tmp15 f64)
    (local $tmp16 f64)
    (local $tmp17 f64)
    (local $tmp18 f64)
  f64.const 5.0
  local.set $tmp0
  local.get $tmp0
  global.set $g_a_main
  f64.const 10.0
  local.set $tmp1
  local.get $tmp1
  global.set $g_b_main
  f64.const 15.0
  local.set $tmp2
  local.get $tmp2
  global.set $g_c_main
  global.get $g_b_main
  local.set $tmp3
  global.get $g_a_main
  local.set $tmp4
  global.get $g_c_main
  local.set $tmp5
  local.get $tmp4
  local.get $tmp3
  local.get $tmp5
  call $fn_sumar_tres_main
  local.set $tmp6
  local.get $tmp6
  global.set $g_resultado_main
  global.get $g_c_main
  local.set $tmp7
  global.get $g_a_main
  local.set $tmp8
  global.get $g_b_main
  local.set $tmp9
  local.get $tmp9
  local.get $tmp8
  local.get $tmp7
  call $fn_sumar_tres_main
  local.set $tmp10
  local.get $tmp10
  global.set $g_resultado_main
  global.get $g_a_main
  local.set $tmp11
  global.get $g_b_main
  local.set $tmp12
  global.get $g_c_main
  local.set $tmp13
  local.get $tmp11
  local.get $tmp12
  local.get $tmp13
  call $fn_sumar_tres_main
  local.set $tmp14
  local.get $tmp14
  global.set $g_resultado_main
  global.get $g_a_main
  local.set $tmp15
  global.get $g_b_main
  local.set $tmp16
  global.get $g_c_main
  local.set $tmp17
  local.get $tmp15
  local.get $tmp16
  local.get $tmp17
  call $fn_sumar_tres_main
  local.set $tmp18
  local.get $tmp18
  global.set $g_resultado_main
  i32.const 0
  )
)
