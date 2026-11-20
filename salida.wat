(module
  (import "env" "log" (func $log (param i32)))
  (import "env" "logf" (func $logf (param f64)))
  (import "env" "logs" (func $logs (param i32 i32)))
  (import "env" "abort" (func $abort (param i32)))
  (memory (export "memory") 1)
  (global $g_estado_main (mut i32) (i32.const 0))
  (global $g_valor_main (mut i32) (i32.const 0))
  (func $__main (export "main") (result i32)
    (local $tmp0 i32)
    (local $tmp1 i32)
  i32.const 3
  local.set $tmp0
  local.get $tmp0
  global.set $g_valor_main
  global.get $g_valor_main
  local.set $tmp1
  local.get $tmp1
  i32.const 1
  i32.eq
  local.get $tmp1
  i32.const 2
  i32.eq
  i32.or
  i32.eqz
  if
    i32.const 5
  call $abort
  unreachable
  end
  local.get $tmp1
  global.set $g_estado_main
  i32.const 0
  )
)
