'use strict';

const fs = require('fs');

const archivo = process.argv[2];
if (!archivo) {
  console.error('Uso: node ejecutar.js archivo.wasm');
  process.exit(1);
}

let bytes;
try {
  bytes = fs.readFileSync(archivo);
} catch (error) {
  console.error('No se pudo leer ' + archivo + ': ' + error.message);
  process.exit(1);
}

let memoria = null;
const mensajes = {
  1: 'division por cero',
  2: 'overflow en suma de enteros',
  3: 'overflow en suma de punto flotante',
  4: 'resultado negativo en resta de enteros sin signo',
  5: 'asignacion invalida a enumeracion'
};

const imports = {
  env: {
    log(valor) {
      console.log(valor);
    },
    logf(valor) {
      console.log(valor);
    },
    logs(puntero, longitud) {
      if (!memoria) {
        console.error('El modulo no exporta memoria para logs');
        return;
      }
      const texto = Buffer.from(memoria.buffer, puntero, longitud).toString('utf8');
      process.stdout.write(texto + '\n');
    },
    abort(codigo) {
      console.error('ERROR EN TIEMPO DE EJECUCION ' + codigo + ': ' + (mensajes[codigo] || 'desconocido'));
      throw new Error('abort:' + codigo);
    }
  }
};

WebAssembly.instantiate(bytes, imports).then(({ instance }) => {
  memoria = instance.exports.memory;
  if (!memoria) {
    throw new Error('El modulo no exporta memoria');
  }
  const resultado = instance.exports.main();
  console.log('RESULTADO_WASM=' + resultado);
}).catch((error) => {
  const mensaje = error && error.message ? error.message : String(error);
  if (!mensaje.startsWith('abort:')) {
    console.error(mensaje);
  }
  process.exit(1);
});
