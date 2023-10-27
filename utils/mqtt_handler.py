import paho.mqtt.client as mqtt

client = mqtt.Client()


def start_mqttclient(mqttport):
    client.on_connect = on_connect
    client.connect("localhost", mqttport, 60)
    client.loop_start()
    return client


def on_connect(client, userdata, flags, rc):
    print("Connected with result code " + str(rc))
