(module
  (import "env" "log" (func $log (param i32)))
  (import "env" "logf" (func $logf (param f64)))
  (import "env" "logs" (func $logs (param i32 i32)))
  (import "env" "abort" (func $abort (param i32)))
  (memory (export "memory") 1)
  (global $g_resultado_transf_main (mut i32) (i32.const 0))
  (func $fn_auditar_sucursal_main_auditor_externo (param $self i32) (param $l_balance_minimo_main_auditor_externo_auditar_sucursal f64) (result f64)
  local.get $l_balance_minimo_main_auditor_externo_auditar_sucursal
  f64.const 1.1
  f64.mul
  return
  f64.const 0.0
  return
  )
  (func $fn_procesar_transferencia_main (param $l_origen_main_procesar_transferencia i32) (param $l_monto_main_procesar_transferencia f64) (result i32)
    (local $l_confirmacion_main_procesar_transferencia i32)
    (local $tmp0 i32)
    (local $tmp1 i32)
  local.get $l_monto_main_procesar_transferencia
  f64.const 0.0
  f64.gt
  if
      i32.const 1
      local.set $tmp0
      local.get $tmp0
      local.set $l_confirmacion_main_procesar_transferencia
 else
      i32.const 0
      local.set $tmp1
      local.get $tmp1
      local.set $l_confirmacion_main_procesar_transferencia
  end
  local.get $l_confirmacion_main_procesar_transferencia
  return
  i32.const 0
  return
  )
  (func $fn_verificar_reserva_main_sistema_bancario (param $self i32) (param $l_monto_solicitado_main_sistema_bancario_verificar_reserva f64) (result i32)
  local.get $self
  i32.const 16
  i32.add
  f64.load
  local.get $l_monto_solicitado_main_sistema_bancario_verificar_reserva
  f64.ge
  if
      i32.const 1
      return
 else
      i32.const 0
      return
  end
  i32.const 0
  return
  )
  (func $__main (export "main") (result i32)
    (local $tmp0 i32)
  i32.const 0
  local.set $tmp0
  local.get $tmp0
  global.set $g_resultado_transf_main
  i32.const 0
  )
)
