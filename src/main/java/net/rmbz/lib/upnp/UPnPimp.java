package net.rmbz.lib.upnp;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by RMBZ.NET on 2015/2/10.
 */
public class UPnPimp implements UPnP {

    private static GatewayDevice activeGW = null;
    private static GatewayDiscover gatewayDiscover = null;

    public UPnPimp() throws ParserConfigurationException, SAXException, IOException {
        activeGW = getGateway();
    }


    /**
     * 验证网关
     *
     * @return
     */
    public GatewayDevice getGateway() throws IOException, SAXException, ParserConfigurationException {
        if (gatewayDiscover == null)
            gatewayDiscover = new GatewayDiscover();
        Map<InetAddress, GatewayDevice> gateways = gatewayDiscover.discover();
        if (gateways.isEmpty()) {
            addLogLine("没有找到网关设备");
            return null;
        }
        addLogLine("找到 " + gateways.size() + " 个网关\n");
        int counter = 0;
        for (GatewayDevice gw : gateways.values()) {
            counter++;
            addLogLine("网关设备详情 #" + counter +
                    "\n\t设备名称: " + gw.getFriendlyName() +
                    "\n\t设备地址: " + gw.getPresentationURL() +
                    "\n\t设备名称: " + gw.getModelName() +
                    "\n\t设备型号: " + gw.getModelNumber() +
                    "\n\t本地地址: " + gw.getLocalAddress().getHostAddress() + "\n");
        }
        GatewayDevice activeGW = gatewayDiscover.getValidGateway();
        if (null != activeGW) {
            addLogLine("活动网关 " + activeGW.getFriendlyName());
            return activeGW;
        } else {
            addLogLine("没有发现活动的网关设备");
            return null;
        }
    }

    /**
     * 获取网关已有UPnP条数(需要设备支持)
     *
     * @return UPnP条数
     */
    public Integer getUPnPCount() throws IOException, SAXException, ParserConfigurationException {
        if (activeGW == null)
            activeGW = getGateway();
        Integer portMapCount = activeGW.getPortMappingNumberOfEntries();
        Integer upnpCount = portMapCount == null ? -1 : portMapCount;
        addLogLine("UPnP条数: " + (upnpCount == -1 ? "(不支持)" : upnpCount));
        return portMapCount;
    }

    public boolean checkPort(int externalPort,
                             String protocol) throws IOException, SAXException, ParserConfigurationException {
        if (activeGW == null)
            activeGW = getGateway();
        PortMappingEntry portMapping = new PortMappingEntry();
        if (activeGW.getSpecificPortMappingEntry(externalPort, protocol, portMapping)) {
            addLogLine("端口 " + externalPort + " 被占用.");
            return false;
        } else {
            addLogLine("端口 " + externalPort + " 可用，准备注册");
            return true;
        }
    }

    public boolean addPortMapping(int externalPort, int internalPort,
                                  String internalClient, String protocol, String description) throws IOException, SAXException, ParserConfigurationException {
        if (activeGW == null)
            activeGW = getGateway();
        if (checkPort(externalPort,protocol)) {
            return activeGW.addPortMapping(externalPort, internalPort, internalClient, protocol, description);
        }
        else {
            return false;
        }
    }

    /**
     * 删除映射
     *
     * @param externalPort
     * @param protocol
     * @return
     * @throws IOException
     * @throws SAXException
     */
    public boolean deletePortMapping(int externalPort, String protocol) throws IOException, SAXException, ParserConfigurationException {
        if (activeGW == null)
            activeGW = getGateway();
        return activeGW.deletePortMapping(externalPort, protocol);
    }

    private void addLogLine(String line) {
        String timeStamp = DateFormat.getTimeInstance().format(new Date());
        String logline = timeStamp + ": " + line + "\n";
        System.out.print(logline);
    }

}
