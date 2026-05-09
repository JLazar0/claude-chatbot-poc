# CLAUDE CHATBOT POC
Prueba de concepto para crear un chatbot con CLAUDE

## Requisitos
 * JDK 21
 * Claude
 * Render [Opcional]



## Fases desarrollo
 * Configurar la comunicación con CLAUDE, 
usando WebClient de WebFlux, pese a contar con otras opciones mejores para java
como puede ser el Anthropic SDK o el starter de spring, más parecido a la certificación
de Architect y al ser peticiones puras, es algo más teórico que nos permite abstraernos
de la tecnología utilizada para entender Claude.
   * Defino variable de entorno **ANTHROPIC_API_KEY**
 * Elijo arquitectura por capas por estar más familiarizado
y ser más sencillo para este POC.
 * Hago una primera comunicación con claude usando la api invocada a través de un web client
 * Cambio la respuesta a streaming
 * Añado memoria al chat (Mapa en memoria con un guid por cada sesión) lo que permite guardar un contexto limitado
   * Deberia ser redis o algo similar 
 * Añado una herramienta para obtener la hora del sistema 
   * Definir y ejecutar
   * Mandar la respuesta a Claude para que devuelva un stream limpio y procese información si es necesario
   
           Usuario -> Claude -> tool_use -> Java ejecuta tool -> tool_result -> Claude genera respuesta final
     Tools en Claude son conversational continuations, no RPC directos.
 * Creo un servicio para las herramientas, separando responsabilidades y creo un tool-handler del que
implementaran todas las tools, lo que permite inyectarlo como Lista de tools en el propio servicio.
 * Como el uso de erramientas implica una conversación continua, cambio el envio de mensajes para que en
el caso de ser Tools sea recursivo.
 * Registro en render para despliegue en contenedor docker
   * Plan hobby -> free
   * Conexion directa con cuenta de github
   * uso de dockerfile de temurin


## Pruebas
### Llamada chatbot con stream e historico en memoria

Se provee un powershell para ejecutar, llamadas de ejemplo por ejemplo:

 * curl -N -H "Accept: text/event-stream" -H "Content-Type: application/json" -X POST http://localhost:8081/chat/stream -d "{\"sessionId\":1,\"message\":\"Hola, me llamo Julio\"}"
 * curl -N -H "Accept: text/event-stream" -H "Content-Type: application/json" -X POST http://localhost:8081/chat/stream -d "{\"sessionId\":1,\"message\":\"¿Recuerdas como me llamo?\"}"

## Resumen de gastos (incremental)
 * Primera jornada, chatbot con historico en memoria
   * 0.03USD, 3h aprox.
   * Total de tokens de entrada 1.386
   * Total de tokens de salida 5.186
 * Segunda jornada, uso de tooling, se tipan los eventos y se mejora el buffering de la respuesta
   * 0.09US, 6h aprox.
   * Total de tokens de entrada 24.271
   * Total de tokens de salida 12.790
 * Tercera jornada, despliegue de micro en render y seguir ajustando el buffering.
   * 0.13US, 8h
   * Total de tokens de entrada 44.279
   * Total de tokens de salida 16.750
