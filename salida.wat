(module
  (import "env" "log" (func $log (param i32)))
  (import "env" "logf" (func $logf (param f64)))
  (import "env" "logs" (func $logs (param i32 i32)))
  (import "env" "abort" (func $abort (param i32)))
  (memory (export "memory") 1)
  (global $g_a_main (mut i32) (i32.const 0))
  (global $g_b_main (mut i32) (i32.const 0))
  (global $g_c_main (mut i32) (i32.const 0))
  (global $g_d_main (mut i32) (i32.const 0))
  (global $g_x_main (mut f64) (f64.const 0.0))
  (global $g_y_main (mut f64) (f64.const 0.0))
  (global $g_z_main (mut f64) (f64.const 0.0))
  (func $__main (export "main") (result i32)
    (local $tmp0 i32)
    (local $tmp1 i32)
    (local $tmp2 f64)
    (local $tmp3 f64)
    (local $tmp4 f64)
    (local $tmp5 i32)
    (local $tmp6 i32)
    (local $tmp7 i32)
    (local $tmp8 i32)
    (local $tmp9 i32)
    (local $tmp10 i32)
    (local $tmp11 i32)
    (local $tmp12 i32)
    (local $tmp13 i32)
    (local $tmp14 i32)
    (local $tmp15 i32)
    (local $tmp16 i32)
    (local $tmp17 i32)
    (local $tmp18 i32)
    (local $tmp19 f64)
    (local $tmp20 f64)
    (local $tmp21 f64)
    (local $tmp22 f64)
    (local $tmp23 f64)
  i32.const 10
  local.set $tmp0
  local.get $tmp0
  global.set $g_b_main
  local.get $tmp0
  local.set $tmp1
  local.get $tmp1
  global.set $g_a_main
  f64.const 1.0
  local.set $tmp2
  local.get $tmp2
  global.set $g_z_main
  global.get $g_z_main
  local.set $tmp3
  local.get $tmp3
  global.set $g_y_main
  local.get $tmp3
  f64.const 2.5
  f64.sub
  local.set $tmp4
  local.get $tmp4
  global.set $g_x_main
  global.get $g_c_main
  local.set $tmp5
  local.get $tmp5
  global.set $g_b_main
  local.get $tmp5
  i32.const 5
  local.set $tmp6
  local.get $tmp6
  global.set $g_d_main
  local.get $tmp6
  i32.add
  local.set $tmp7
  local.get $tmp7
  i32.const 255
  i32.gt_u
  if
    i32.const 2
  call $abort
  unreachable
  end
  local.get $tmp7
  local.set $tmp8
  local.get $tmp8
  global.set $g_a_main
  i32.const 15
  local.set $tmp9
  local.get $tmp9
  global.set $g_a_main
  local.get $tmp9
  i32.const 15
  i32.eq
  if
      global.get $g_a_main
      local.set $tmp10
      local.get $tmp10
      global.set $g_b_main
 else
      i32.const 0
      local.set $tmp11
      local.get $tmp11
      global.set $g_b_main
  end
  global.get $g_b_main
  local.set $tmp12
  local.get $tmp12
  global.set $g_a_main
  local.get $tmp12
  global.get $g_c_main
  i32.add
  local.set $tmp13
  local.get $tmp13
  i32.const 255
  i32.gt_u
  if
    i32.const 2
  call $abort
  unreachable
  end
  local.get $tmp13
  global.get $g_b_main
  local.set $tmp14
  local.get $tmp14
  global.set $g_d_main
  local.get $tmp14
  i32.eq
  if
      i32.const 10
      local.set $tmp15
      local.get $tmp15
      global.set $g_a_main
      global.get $g_b_main
      local.set $tmp16
      local.get $tmp16
      global.set $g_a_main
      local.get $tmp16
      i32.const 14
      i32.add
      local.set $tmp17
      local.get $tmp17
      i32.const 255
      i32.gt_u
      if
        i32.const 2
      call $abort
      unreachable
      end
      local.get $tmp17
      local.set $tmp18
      local.get $tmp18
      global.set $g_b_main
 else
      global.get $g_y_main
      local.set $tmp19
      local.get $tmp19
      global.set $g_z_main
      local.get $tmp19
      global.get $g_x_main
      f64.mul
      local.set $tmp20
      local.get $tmp20
      global.set $g_x_main
  end
  global.get $g_x_main
  global.get $g_x_main
  local.set $tmp21
  local.get $tmp21
  global.set $g_y_main
  local.get $tmp21
  global.get $g_z_main
  f64.add
  local.set $tmp22
  local.get $tmp22
  f64.abs
  f64.const 1.7976931348623157e308
  f64.gt
  if
    i32.const 3
  call $abort
  unreachable
  end
  local.get $tmp22
  f64.eq
  if
      f64.const 2.0
      local.set $tmp23
      local.get $tmp23
      global.set $g_x_main
  end
  i32.const 0
  )
)
