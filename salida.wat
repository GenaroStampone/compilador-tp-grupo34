(module
  (import "env" "log" (func $log (param i32)))
  (import "env" "logf" (func $logf (param f64)))
  (import "env" "logs" (func $logs (param i32 i32)))
  (import "env" "abort" (func $abort (param i32)))
  (memory (export "memory") 1)
  (global $g_estado_actual_main (mut i32) (i32.const 0))
  (global $g_res_doble_main (mut f64) (f64.const 0.0))
  (global $g_res_func_main (mut i32) (i32.const 0))
  (global $g_val_doble1_main (mut f64) (f64.const 0.0))
  (global $g_val_int1_main (mut i32) (i32.const 0))
  (global $g_val_int2_main (mut i32) (i32.const 0))
  (func $fn_calcular_bonus_main_procesador_base (param $self i32) (param $l_base_main_procesador_base_calcular_bonus f64) (result f64)
  local.get $l_base_main_procesador_base_calcular_bonus
  f64.const 10.0
  f64.gt
  if
      local.get $l_base_main_procesador_base_calcular_bonus
      f64.const 1.5
      f64.mul
      return
 else
      local.get $l_base_main_procesador_base_calcular_bonus
      return
  end
  f64.const 0.0
  return
  )
  (func $fn_ejecutar_escala_main_procesador_base (param $self i32) (param $l_v1_main_procesador_base_ejecutar_escala f64) (param $l_v2_main_procesador_base_ejecutar_escala i32) (result f64)
    (local $tmp0 f64)
  local.get $l_v1_main_procesador_base_ejecutar_escala
  local.get $self
  i32.const 8
  i32.add
  f64.load
  f64.mul
  local.get $l_v2_main_procesador_base_ejecutar_escala
  f64.convert_i32_u
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
  (func $fn_funcion_auxiliar_main (param $l_p1_main_funcion_auxiliar i32) (param $l_p2_main_funcion_auxiliar f64) (param $l_p3_main_funcion_auxiliar i32) (result i32)
    (local $l_resultado_local_main_funcion_auxiliar i32)
    (local $tmp0 i32)
    (local $tmp1 i32)
  local.get $l_p1_main_funcion_auxiliar
  local.get $l_p3_main_funcion_auxiliar
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
  local.set $tmp1
  local.get $tmp1
  local.set $l_resultado_local_main_funcion_auxiliar
  local.get $l_p2_main_funcion_auxiliar
  f64.const 1.0
  f64.ge
  if
      local.get $l_resultado_local_main_funcion_auxiliar
      return
 else
      i32.const 24
      i32.const 49
      call $logs
  end
  i32.const 0
  return
  )
  (func $__main (export "main") (result i32)
    (local $tmp0 i32)
    (local $tmp1 i32)
    (local $tmp2 f64)
    (local $tmp3 i32)
    (local $tmp4 f64)
    (local $tmp5 i32)
    (local $tmp6 f64)
    (local $tmp7 i32)
    (local $tmp8 i32)
    (local $tmp9 i32)
    (local $tmp10 i32)
    (local $tmp11 i32)
    (local $tmp12 f64)
    (local $tmp13 i32)
    (local $tmp14 i32)
    (local $tmp15 i32)
    (local $tmp16 i32)
    (local $tmp17 i32)
    (local $tmp18 f64)
    (local $tmp19 i32)
    (local $tmp20 f64)
    (local $tmp21 f64)
  i32.const 5
  local.set $tmp0
  local.get $tmp0
  global.set $g_val_int1_main
  i32.const 10
  local.set $tmp1
  local.get $tmp1
  global.set $g_val_int2_main
  f64.const 2.5
  local.set $tmp2
  local.get $tmp2
  global.set $g_val_doble1_main
  i32.const 1
  local.set $tmp3
  local.get $tmp3
  global.set $g_estado_actual_main
  f64.const 2.0
  local.set $tmp4
  i32.const 0
  i32.const 8
  i32.add
  local.get $tmp4
  f64.store
  global.get $g_val_int1_main
  local.set $tmp5
  global.get $g_val_doble1_main
  local.set $tmp6
  i32.const 15
  local.set $tmp7
  local.get $tmp7
  global.set $g_val_int2_main
  local.get $tmp7
  local.set $tmp8
  local.get $tmp5
  local.get $tmp6
  local.get $tmp8
  call $fn_funcion_auxiliar_main
  local.set $tmp9
  local.get $tmp9
  global.set $g_res_func_main
  i32.const 2
  local.set $tmp10
  local.get $tmp10
  global.set $g_val_int1_main
  local.get $tmp10
  local.set $tmp11
  f64.const 1.0
  local.set $tmp12
  global.get $g_val_int2_main
  local.set $tmp13
  local.get $tmp11
  local.get $tmp12
  local.get $tmp13
  call $fn_funcion_auxiliar_main
  local.set $tmp14
  local.get $tmp14
  global.set $g_res_func_main
  block $salida0
    loop $ciclo1
      global.get $g_val_int1_main
      global.get $g_val_int2_main
      i32.lt_u
      i32.eqz
      br_if $salida0
      global.get $g_val_int1_main
      i32.const 1
      i32.add
      local.set $tmp15
      local.get $tmp15
      i32.const 255
      i32.gt_u
      if
        i32.const 2
      call $abort
      unreachable
      end
      local.get $tmp15
      local.set $tmp16
      local.get $tmp16
      global.set $g_val_int1_main
      global.get $g_val_int1_main
      i32.const 10
      i32.eq
      if
          i32.const 80
          i32.const 36
          call $logs
     else
          i32.const 0
          local.set $tmp17
          global.get $g_val_doble1_main
          local.set $tmp18
          global.get $g_val_int1_main
          local.set $tmp19
          local.get $tmp17
          local.get $tmp18
          local.get $tmp19
          call $fn_ejecutar_escala_main_procesador_base
          call $logf
      end
      br $ciclo1
    end
  end
  i32.const 0
  i32.const 8
  i32.add
  f64.load
  global.get $g_val_doble1_main
  f64.add
  local.set $tmp20
  local.get $tmp20
  f64.abs
  f64.const 1.7976931348623157e308
  f64.gt
  if
    i32.const 3
  call $abort
  unreachable
  end
  local.get $tmp20
  local.set $tmp21
  local.get $tmp21
  global.set $g_res_doble_main
  i32.const 120
  i32.const 40
  call $logs
  i32.const 0
  )
  (data (i32.const 24) "Modo de ejecucion estandar sin retorno expl\c3\adcito")
  (data (i32.const 80) "Limite alcanzado en bucle de control")
  (data (i32.const 120) "Programa valido finalizado correctamente")
)
