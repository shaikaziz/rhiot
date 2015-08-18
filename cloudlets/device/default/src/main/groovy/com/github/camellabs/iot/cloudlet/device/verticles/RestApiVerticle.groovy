/**
 * Licensed to the Camel Labs under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.camellabs.iot.cloudlet.device.verticles

import com.github.camellabs.iot.cloudlet.device.vertx.BaseRestApiVerticle
import io.vertx.core.Future
import io.vertx.core.Handler
import io.vertx.groovy.core.buffer.Buffer

import static com.github.camellabs.iot.cloudlet.device.verticles.LeshanServerVeritcle.CHANNEL_DEVICES_DISCONNECTED
import static com.github.camellabs.iot.cloudlet.device.vertx.BaseRestApiVerticle.*
import static com.github.camellabs.iot.vertx.PropertyResolver.intProperty
import static io.vertx.core.http.HttpMethod.DELETE
import static io.vertx.core.http.HttpMethod.GET
import static io.vertx.core.http.HttpMethod.POST
import static io.vertx.groovy.ext.web.Router.router

class RestApiVerticle extends BaseRestApiVerticle {

    @Override
    void start(Future<Void> startFuture) {
        vertx.runOnContext {
            def http = vertx.createHttpServer()
            def router = router(vertx)

            // Get list of clients
            router.route('/device').method(GET).handler { rc ->
                vertx.eventBus().send('listDevices', null, { clients -> jsonResponse(rc, clients) })
            }

            // Get list of disconnected clients' identifiers
            router.route('/device/disconnected').method(GET).handler { rc ->
                vertx.eventBus().send(CHANNEL_DEVICES_DISCONNECTED, null, { clients -> jsonResponse(rc, clients) })
            }

            router.route('/client').method(POST).handler { rc ->
                rc.request().bodyHandler(new Handler<Buffer>(){
                    @Override
                    void handle(Buffer event) {
                        vertx.eventBus().send('clients.create.virtual', event.toString('utf-8'), { status -> jsonResponse(rc, status) })
                    }
                })
            }

            router.route("/client").method(DELETE).handler { rc ->
                vertx.eventBus().send('deleteClients', null, { status -> jsonResponse(rc, status) })
            }

            router.route("/client/:clientId").method(GET).handler { rc ->
                vertx.eventBus().send('getClient', parameter(rc, 'clientId')) { client -> jsonResponse(rc, client) }
            }

            router.route("/client/:clientId/manufacturer").method(GET).handler { rc ->
                vertx.eventBus().send('client.manufacturer', parameter(rc, 'clientId')) { client ->
                    jsonResponse(rc, client)
                }
            }

            router.route("/client/:clientId/model").method(GET).handler { rc ->
                vertx.eventBus().send('client.model', parameter(rc, 'clientId')) { client ->
                    jsonResponse(rc, client)
                }
            }

            router.route("/client/:clientId/serial").method(GET).handler { rc ->
                vertx.eventBus().send('client.serial', parameter(rc, 'clientId')) { client ->
                    jsonResponse(rc, client)
                }
            }

            http.requestHandler(router.&accept).listen(intProperty('camellabs_iot_cloudlet_device_api_rest_port', 15000))

            startFuture.complete()
        }
    }

}
