package net.rmbz.lib.upnp;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by RMBZ.NET on 2015/2/10.
 */
public interface UPnP {

    /**
     * 验证网关
     *
     * @return
     */
    GatewayDevice getGateway() throws IOException, SAXException, ParserConfigurationException;

    /**
     * 获取网关已有UPnP条数(需要设备支持)
     *
     * @return UPnP条数
     */
    Integer getUPnPCount() throws IOException, SAXException, ParserConfigurationException;

    boolean checkPort(int externalPort,
                      String protocol) throws IOException, SAXException, ParserConfigurationException;

    boolean addPortMapping(int externalPort, int internalPort,
                           String internalClient, String protocol, String description) throws IOException, SAXException, ParserConfigurationException;

    /**
     * 删除映射
     *
     * @param externalPort
     * @param protocol
     * @return
     */
    boolean deletePortMapping(int externalPort, String protocol) throws IOException, SAXException, ParserConfigurationException;

}
