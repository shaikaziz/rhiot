/**
 * Licensed to the Rhiot under one or more
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
package io.rhiot.cloudplatform.camel.vertxproton;

import io.vertx.core.Vertx;
import io.vertx.proton.ProtonClient;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

public class ProtonjEndpoint extends DefaultEndpoint {

    private Vertx vertx;

    private ProtonClient protonClient;

    private String address;

    private AmqpAddress addressParser;

    public ProtonjEndpoint(String endpointUri, String address, Component component) {
        super(endpointUri, component);
        this.address = address;
        this.addressParser = new AmqpAddress(address);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new ProtonjProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new ProtonjConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public ProtonjComponent getComponent() {
        return (ProtonjComponent) super.getComponent();
    }

    public Vertx getVertx() {
        if(vertx != null) {
            return vertx;
        }

        if(getComponent().getVertx() != null) {
            vertx = getComponent().getVertx();
        } else {
            vertx = Vertx.vertx();
        }
        return vertx;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
    }

    public ProtonClient getProtonClient() {
        if(protonClient != null) {
            return protonClient;
        }

        if(getComponent().getProtonClient() != null) {
            protonClient = getComponent().getProtonClient();
        } else {
            protonClient = ProtonClient.create(getVertx());
        }
        return protonClient;
    }

    public void setProtonClient(ProtonClient protonClient) {
        this.protonClient = protonClient;
    }

    public String getAddress() {
        if(address != null && !address.isEmpty()) {
            return address;
        } else if(getComponent().getAddress() != null) {
            return getComponent().getAddress();
        }
        throw new IllegalStateException("No AMQP address specified.");
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AmqpAddress addressParser() {
        return addressParser;
    }

}
