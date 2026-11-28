(module
  (import "env" "log" (func $log (param i32)))
  (import "env" "logf" (func $logf (param f64)))
  (import "env" "logs" (func $logs (param i32 i32)))
  (import "env" "abort" (func $abort (param i32)))
  (memory (export "memory") 1)
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
  (global $g_resultado_transf_main (mut i32) (i32.const 0))
  (func $fn_auditar_sucursal_main_auditor_externo (param $self i32) (param $l_balance_minimo_main_auditor_externo_auditar_sucursal f64) (result f64)
  local.get $l_balance_minimo_main_auditor_externo_auditar_sucursal
  f64.const 1.1
  f64.mul
=======
=======
>>>>>>> parent of c9718f9 (Revision casos de prueba 47-81)
  (global $g_acum_while_main (mut i32) (i32.const 0))
  (global $g_contador_main (mut i32) (i32.const 0))
  (global $g_div_zero_dyn_main (mut i32) (i32.const 0))
  (global $g_estado_glob_main (mut i32) (i32.const 0))
  (global $g_factor_global_main (mut f64) (f64.const 0.0))
  (global $g_ov_float1_main (mut f64) (f64.const 0.0))
  (global $g_ov_float2_main (mut f64) (f64.const 0.0))
  (global $g_ov_int1_main (mut i32) (i32.const 0))
  (global $g_ov_int2_main (mut i32) (i32.const 0))
  (global $g_res_audit_main (mut i32) (i32.const 0))
  (global $g_res_eval_main (mut i32) (i32.const 0))
  (global $g_res_flotante_main (mut f64) (f64.const 0.0))
  (global $g_res_normal_main (mut i32) (i32.const 0))
  (global $g_test_error_main (mut i32) (i32.const 0))
  (global $g_v_arg_main (mut i32) (i32.const 0))
  (global $g_valor_main (mut i32) (i32.const 0))
  (func $fn_auditar_sistema_main_gestor_amigo_impl (param $self i32) (param $l_s_main_gestor_amigo_impl_auditar_sistema i32) (result i32)
    (local $l_valor_main_gestor_amigo_impl_auditar_sistema i32)
    (local $tmp0 i32)
    (local $tmp1 i32)
  i32.const 50
  local.set $tmp0
  local.get $tmp0
  local.set $l_valor_main_gestor_amigo_impl_auditar_sistema
  local.get $l_s_main_gestor_amigo_impl_auditar_sistema
  i32.const 8
  i32.add
  i32.load
  local.get $l_valor_main_gestor_amigo_impl_auditar_sistema
  i32.add
  local.set $tmp1
  local.get $tmp1
  i32.const 255
  i32.gt_u
  if
    i32.const 2
  call $abort
  unreachable
  end
  local.get $tmp1
>>>>>>> parent of 4964f2c (Revision casos de prueba 30-39)
=======
  (global $g_estado_actual_main (mut i32) (i32.const 0))
  (global $g_resultado_final_main (mut f64) (f64.const 0.0))
  (global $g_v_entero_main (mut i32) (i32.const 0))
  (global $g_v_flotante_main (mut f64) (f64.const 0.0))
  (func $fn_auditar_sistema_main_auditor_amigo_impl (param $self i32) (param $l_p_main_auditor_amigo_impl_auditar_sistema i32) (result f64)
  local.get $l_p_main_auditor_amigo_impl_auditar_sistema
  i32.const 8
  i32.add
  f64.load
>>>>>>> parent of 79b7188 (Verificacion casos de prueba 40-46)
  return
  f64.const 0.0
  return
  )
<<<<<<< HEAD
<<<<<<< HEAD
  (func $fn_procesar_transferencia_main (param $l_origen_main_procesar_transferencia i32) (param $l_monto_main_procesar_transferencia f64) (result i32)
    (local $l_confirmacion_main_procesar_transferencia i32)
    (local $tmp0 i32)
    (local $tmp1 i32)
  local.get $l_monto_main_procesar_transferencia
  f64.const 0.0
  f64.gt
=======
  (func $fn_cargar_pin_main_base_alfa (param $self i32) (param $l_p_main_base_alfa_cargar_pin i32) (result i32)
    (local $tmp0 i32)
  local.get $l_p_main_base_alfa_cargar_pin
  local.set $tmp0
  local.get $self
  i32.const 8
  i32.add
  local.get $tmp0
  i32.store
  local.get $self
  i32.const 8
  i32.add
  i32.load
  return
  i32.const 0
  return
  )
  (func $fn_evaluar_orden_main (param $l_valor_main_evaluar_orden i32) (param $l_b_main_evaluar_orden i32) (param $l_c_main_evaluar_orden i32) (result i32)
    (local $tmp0 i32)
    (local $tmp1 i32)
  local.get $l_valor_main_evaluar_orden
  local.get $l_b_main_evaluar_orden
  i32.add
  local.set $tmp0
  local.get $tmp0
  i32.const 255
  i32.gt_u
>>>>>>> parent of 4964f2c (Revision casos de prueba 30-39)
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
<<<<<<< HEAD
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
=======
  local.get $tmp0
  local.get $l_c_main_evaluar_orden
  i32.add
  local.set $tmp1
  local.get $tmp1
  i32.const 255
  i32.gt_u
  if
    i32.const 2
  call $abort
  unreachable
>>>>>>> parent of 4964f2c (Revision casos de prueba 30-39)
  end
  local.get $tmp1
  return
  i32.const 0
  return
  )
  (func $__main (export "main") (result i32)
    (local $tmp0 i32)
<<<<<<< HEAD
  i32.const 0
  local.set $tmp0
  local.get $tmp0
  global.set $g_resultado_transf_main
  i32.const 0
  )
=======
    (local $tmp1 f64)
    (local $tmp2 i32)
    (local $tmp3 i32)
    (local $tmp4 i32)
    (local $tmp5 i32)
    (local $tmp6 f64)
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
    (local $tmp19 i32)
    (local $tmp20 i32)
    (local $tmp21 i32)
    (local $tmp22 i32)
    (local $tmp23 i32)
    (local $tmp24 i32)
    (local $tmp25 i32)
    (local $tmp26 i32)
    (local $tmp27 i32)
    (local $tmp28 i32)
    (local $tmp29 i32)
    (local $tmp30 f64)
    (local $tmp31 f64)
    (local $tmp32 i32)
    (local $tmp33 i32)
    (local $tmp34 i32)
    (local $tmp35 i32)
    (local $tmp36 i32)
    (local $tmp37 i32)
    (local $tmp38 i32)
    (local $tmp39 f64)
    (local $tmp40 f64)
    (local $tmp41 f64)
    (local $tmp42 f64)
  i32.const 10
  local.set $tmp0
  local.get $tmp0
  global.set $g_valor_main
  f64.const 2.0
  local.set $tmp1
  local.get $tmp1
  global.set $g_factor_global_main
  i32.const 20
  local.set $tmp2
  local.get $tmp2
  global.set $g_estado_glob_main
  i32.const 8
  local.set $tmp3
  i32.const 123
  local.set $tmp4
  local.get $tmp3
  local.get $tmp4
  call $fn_cargar_pin_main_base_alfa
  local.set $tmp5
  local.get $tmp5
  global.set $g_res_audit_main
  f64.const 100.0
  local.set $tmp6
  i32.const 8
  i32.const 0
  i32.add
  local.get $tmp6
  f64.store
  i32.const 5
  local.set $tmp7
  i32.const 8
  i32.const 16
  i32.add
  local.get $tmp7
  i32.store
  i32.const 7
  local.set $tmp8
  i32.const 8
  i32.const 32
  i32.add
  local.get $tmp8
  i32.store
  i32.const 0
  local.set $tmp9
  i32.const 8
  local.set $tmp10
  local.get $tmp9
  local.get $tmp10
  call $fn_auditar_sistema_main_gestor_amigo_impl
  local.set $tmp11
  local.get $tmp11
  global.set $g_res_audit_main
  i32.const 5
  local.set $tmp12
  local.get $tmp12
  global.set $g_v_arg_main
  i32.const 20
  local.set $tmp13
  local.get $tmp13
  global.set $g_v_arg_main
  local.get $tmp13
  local.set $tmp14
  global.get $g_v_arg_main
  local.set $tmp15
  global.get $g_v_arg_main
  local.set $tmp16
  local.get $tmp15
  local.get $tmp14
  local.get $tmp16
  call $fn_evaluar_orden_main
  local.set $tmp17
  local.get $tmp17
  global.set $g_res_eval_main
  i32.const 5
  local.set $tmp18
  local.get $tmp18
  global.set $g_v_arg_main
  global.get $g_v_arg_main
  local.set $tmp19
  i32.const 20
  local.set $tmp20
  local.get $tmp20
  global.set $g_v_arg_main
  local.get $tmp20
  local.set $tmp21
  global.get $g_v_arg_main
  local.set $tmp22
  local.get $tmp19
  local.get $tmp21
  local.get $tmp22
  call $fn_evaluar_orden_main
  local.set $tmp23
  local.get $tmp23
  global.set $g_res_normal_main
  i32.const 0
  local.set $tmp24
  local.get $tmp24
  global.set $g_acum_while_main
  i32.const 0
  local.set $tmp25
  local.get $tmp25
  global.set $g_contador_main
  block $salida0
    loop $ciclo1
      global.get $g_contador_main
      i32.const 3
      i32.lt_u
      i32.eqz
      br_if $salida0
      global.get $g_acum_while_main
      i32.const 10
      i32.add
      local.set $tmp26
      local.get $tmp26
      i32.const 255
      i32.gt_u
      if
        i32.const 2
      call $abort
      unreachable
      end
      local.get $tmp26
      local.set $tmp27
      local.get $tmp27
      global.set $g_acum_while_main
      global.get $g_contador_main
      i32.const 1
      i32.add
      local.set $tmp28
      local.get $tmp28
      i32.const 255
      i32.gt_u
      if
        i32.const 2
      call $abort
      unreachable
      end
      local.get $tmp28
      local.set $tmp29
      local.get $tmp29
      global.set $g_contador_main
      br $ciclo1
    end
  end
  global.get $g_res_eval_main
  f64.convert_i32_u
  global.get $g_factor_global_main
=======
  (func $fn_calcular_bono_main_base_alfa (param $self i32) (param $l_base_calc_main_base_alfa_calcular_bono f64) (result f64)
    (local $tmp0 f64)
  local.get $l_base_calc_main_base_alfa_calcular_bono
  f64.const 10.0
>>>>>>> parent of 79b7188 (Verificacion casos de prueba 40-46)
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
<<<<<<< HEAD
  local.set $tmp32
  local.get $tmp32
  global.set $g_test_error_main
  global.get $g_test_error_main
  i32.const 1
  i32.eq
  if
      i32.const 0
      local.set $tmp33
      local.get $tmp33
      global.set $g_div_zero_dyn_main
      i32.const 100
      local.set $tmp34
      local.get $tmp34
      global.set $g_acum_while_main
 else
      global.get $g_test_error_main
      i32.const 2
      i32.eq
      if
          i32.const 200
          local.set $tmp35
          local.get $tmp35
          global.set $g_ov_int1_main
          i32.const 100
          local.set $tmp36
          local.get $tmp36
          global.set $g_ov_int2_main
          global.get $g_ov_int1_main
          global.get $g_ov_int2_main
          i32.add
          local.set $tmp37
          local.get $tmp37
          i32.const 255
          i32.gt_u
          if
            i32.const 2
          call $abort
          unreachable
          end
          local.get $tmp37
          local.set $tmp38
          local.get $tmp38
          global.set $g_acum_while_main
     else
          global.get $g_test_error_main
          i32.const 3
          i32.eq
          if
              f64.const 1.5e308
              local.set $tmp39
              local.get $tmp39
              global.set $g_ov_float1_main
              f64.const 1.5e308
              local.set $tmp40
              local.get $tmp40
              global.set $g_ov_float2_main
              global.get $g_ov_float1_main
              global.get $g_ov_float2_main
              f64.add
              local.set $tmp41
              local.get $tmp41
              f64.abs
              f64.const 1.7976931348623157e308
              f64.gt
              if
                i32.const 3
              call $abort
              unreachable
              end
              local.get $tmp41
              local.set $tmp42
              local.get $tmp42
              global.set $g_res_flotante_main
         else
              global.get $g_test_error_main
              i32.const 4
              i32.eq
              if
                  i32.const 440
                  i32.const 70
                  call $logs
             else
                  i32.const 512
                  i32.const 68
                  call $logs
              end
          end
      end
  end
=======
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
>>>>>>> 73e59723d7b945a0dae27d8d9801d10dcf16867a
  i32.const 0
  )
  (data (i32.const 48) "--- INICIO TEST COMPLEJO TP1-TP4 GRUPO 34 ---")
  (data (i32.const 96) "Valor global inicial:")
  (data (i32.const 120) "Resultado auditoria friend (pin + valor local):")
  (data (i32.const 168) "Resultado orden permutado:")
  (data (i32.const 200) "Resultado orden normal:")
  (data (i32.const 224) "Acceso posicional c1[0$us] (saldo):")
  (data (i32.const 264) "Acceso posicional c1[2$us] (codigo heredado):")
  (data (i32.const 312) "Acceso posicional c1[3$us] (extra propio):")
  (data (i32.const 360) "Resultado acumulado while:")
  (data (i32.const 392) "Conversion implicita ushortint a doublef:")
  (data (i32.const 440) "Selector 4: Ver comentario en codigo para desacompa\c3\b1ar prueba de enum")
  (data (i32.const 512) "Ejecucion normal completa sin errores de runtime (test_error = 0$us)")
>>>>>>> parent of 4964f2c (Revision casos de prueba 30-39)
=======
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
>>>>>>> parent of 79b7188 (Verificacion casos de prueba 40-46)
)
