package tfar.strawstatuesinteractive.network.client;


import tfar.strawstatuesinteractive.network.ModPacket;

public interface S2CModPacket extends ModPacket {
    void handleClient();
}
