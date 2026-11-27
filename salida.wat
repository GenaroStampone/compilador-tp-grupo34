(module
  (import "env" "log" (func $log (param i32)))
  (import "env" "logf" (func $logf (param f64)))
  (import "env" "logs" (func $logs (param i32 i32)))
  (import "env" "abort" (func $abort (param i32)))
  (memory (export "memory") 1)
  (global $g_estado_actual_main (mut i32) (i32.const 0))
  (global $g_resultado_final_main (mut f64) (f64.const 0.0))
  (global $g_v_entero_main (mut i32) (i32.const 0))
  (global $g_v_flotante_main (mut f64) (f64.const 0.0))
  (func $fn_auditar_sistema_main_auditor_amigo_impl (param $self i32) (param $l_p_main_auditor_amigo_impl_auditar_sistema i32) (result f64)
  local.get $l_p_main_auditor_amigo_impl_auditar_sistema
  i32.const 8
  i32.add
  f64.load
  return
  f64.const 0.0
  return
  )
  (func $fn_calcular_bono_main_base_alfa (param $self i32) (param $l_base_calc_main_base_alfa_calcular_bono f64) (result f64)
    (local $tmp0 f64)
  local.get $l_base_calc_main_base_alfa_calcular_bono
  f64.const 10.0
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
  (func $fn_calcular_bono_main_base_beta (param $self i32) (param $l_base_calc_main_base_beta_calcular_bono f64) (result f64)
    (local $tmp0 f64)
  local.get $l_base_calc_main_base_beta_calcular_bono
  f64.const 50.0
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
  (func $fn_calcular_rendimiento_main (param $l_tasa_main_calcular_rendimiento f64) (param $l_periodo_main_calcular_rendimiento i32) (result f64)
    (local $tmp0 f64)
    (local $tmp1 f64)
  local.get $l_tasa_main_calcular_rendimiento
  local.set $tmp0
  local.get $l_periodo_main_calcular_rendimiento
  f64.convert_i32_u
  local.tee $tmp1
  f64.const 0
  f64.eq
  if
    i32.const 1
  call $abort
  unreachable
  end
  local.get $tmp0
  local.get $tmp1
  f64.div
  return
  f64.const 0.0
  return
  )
  (func $fn_ejecutar_control_main_procesador_central (param $self i32) (param $l_factor_inicial_main_procesador_central_ejecutar_control f64) (param $l_limite_pasos_main_procesador_central_ejecutar_control i32) (result f64)
    (local $l_acumulador_main_procesador_central_ejecutar_control f64)
    (local $l_contador_main_procesador_central_ejecutar_control i32)
    (local $tmp0 i32)
    (local $tmp1 f64)
    (local $tmp2 f64)
    (local $tmp3 f64)
    (local $tmp4 f64)
    (local $tmp5 i32)
    (local $tmp6 i32)
  i32.const 1
  local.set $tmp0
  local.get $tmp0
  local.set $l_contador_main_procesador_central_ejecutar_control
  local.get $l_factor_inicial_main_procesador_central_ejecutar_control
  local.set $tmp1
  local.get $tmp1
  local.set $l_acumulador_main_procesador_central_ejecutar_control
  block $salida0
    loop $ciclo1
      local.get $l_contador_main_procesador_central_ejecutar_control
      local.get $l_limite_pasos_main_procesador_central_ejecutar_control
      i32.le_u
      i32.eqz
      br_if $salida0
      local.get $l_acumulador_main_procesador_central_ejecutar_control
      local.set $tmp2
      local.get $tmp2
      local.set $l_acumulador_main_procesador_central_ejecutar_control
      local.get $tmp2
      local.get $l_contador_main_procesador_central_ejecutar_control
      f64.convert_i32_u
      f64.const 1.5
      f64.mul
      f64.add
      local.set $tmp3
      local.get $tmp3
      f64.abs
      f64.const 1.7976931348623157e308
      f64.gt
      if
        i32.const 3
      call $abort
      unreachable
      end
      local.get $tmp3
      local.set $tmp4
      local.get $tmp4
      local.set $l_acumulador_main_procesador_central_ejecutar_control
      local.get $l_contador_main_procesador_central_ejecutar_control
      i32.const 1
      i32.add
      local.set $tmp5
      local.get $tmp5
      i32.const 255
      i32.gt_u
      if
        i32.const 2
      call $abort
      unreachable
      end
      local.get $tmp5
      local.set $tmp6
      local.get $tmp6
      local.set $l_contador_main_procesador_central_ejecutar_control
      br $ciclo1
    end
  end
  local.get $l_acumulador_main_procesador_central_ejecutar_control
  return
  f64.const 0.0
  return
  )
  (func $fn_funcion_amiga_global_main (param $l_cod_main_funcion_amiga_global i32) (result i32)
    (local $tmp0 i32)
  local.get $l_cod_main_funcion_amiga_global
  i32.const 100
  i32.add
  local.set $tmp0
  local.get $tmp0
  i32.const 255
  i32.gt_u
  if
    i32.const 2
  call $abort
  unreachable
  end
  local.get $tmp0
  return
  i32.const 0
  return
  )
  (func $fn_obtener_factor_beta_main_base_beta (param $self i32) (param $l_aux_main_base_beta_obtener_factor_beta i32) (result f64)
  f64.const 2.5
  return
  f64.const 0.0
  return
  )
  (func $fn_verificar_pin_propio_main_base_alfa (param $self i32) (param $l_entrada_pin_main_base_alfa_verificar_pin_propio i32) (result i32)
  local.get $l_entrada_pin_main_base_alfa_verificar_pin_propio
  local.get $self
  i32.const 16
  i32.add
  i32.load
  i32.eq
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
    (local $tmp1 i32)
    (local $tmp2 i32)
    (local $tmp3 f64)
    (local $tmp4 i32)
    (local $tmp5 f64)
    (local $tmp6 f64)
    (local $tmp7 i32)
    (local $tmp8 f64)
    (local $tmp9 i32)
    (local $tmp10 f64)
    (local $tmp11 i32)
    (local $tmp12 i32)
    (local $tmp13 f64)
    (local $tmp14 f64)
    (local $tmp15 i32)
    (local $tmp16 f64)
  i32.const 2
  local.set $tmp0
  local.get $tmp0
  global.set $g_estado_actual_main
  i32.const 100
  local.set $tmp1
  i32.const 8
  i32.const 32
  i32.add
  local.get $tmp1
  i32.store
  i32.const 5
  local.set $tmp2
  i32.const 8
  i32.const 24
  i32.add
  local.get $tmp2
  i32.store
  f64.const 750.0
  local.set $tmp3
  i32.const 8
  i32.const 8
  i32.add
  local.get $tmp3
  f64.store
  i32.const 8
  local.set $tmp4
  i32.const 20
  f64.convert_i32_u
  local.set $tmp5
  local.get $tmp4
  local.get $tmp5
  call $fn_calcular_bono_main_base_alfa
  local.set $tmp6
  local.get $tmp6
  global.set $g_v_flotante_main
  i32.const 8
  local.set $tmp7
  f64.const 10.0
  local.set $tmp8
  i32.const 3
  local.set $tmp9
  local.get $tmp7
  local.get $tmp8
  local.get $tmp9
  call $fn_ejecutar_control_main_procesador_central
  local.set $tmp10
  local.get $tmp10
  global.set $g_v_flotante_main
  i32.const 0
  local.set $tmp11
  i32.const 8
  local.set $tmp12
  local.get $tmp11
  local.get $tmp12
  call $fn_auditar_sistema_main_auditor_amigo_impl
  local.set $tmp13
  local.get $tmp13
  global.set $g_resultado_final_main
  i32.const 100
  f64.convert_i32_u
  local.set $tmp14
  i32.const 2
  local.set $tmp15
  local.get $tmp14
  local.get $tmp15
  call $fn_calcular_rendimiento_main
  local.set $tmp16
  local.get $tmp16
  global.set $g_resultado_final_main
  i32.const 0
  )
)
