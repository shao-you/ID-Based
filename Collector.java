package net.floodlightcontroller.headerextract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
 
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFPacketOut;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFType;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionDataLayerDestination;
import org.openflow.protocol.action.OFActionNetworkLayerDestination;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.protocol.action.OFActionTransportLayerDestination;
import org.openflow.util.HexString;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
 
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.counter.ICounterStoreService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.learningswitch.LearningSwitch;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.util.OFMessageDamper;
import java.util.Date;
import java.sql.*;
 
public class HeaderExtract implements IOFMessageListener, IFloodlightModule {
 
	public final int DEFAULT_CACHE_SIZE = 10;
	protected IFloodlightProviderService floodlightProvider;
	protected ICounterStoreService counterStore;
    protected OFMessageDamper messageDamper;
	protected IDeviceService deviceManager;
	private IStaticFlowEntryPusherService staticFlowEntryPusher;
	private IRoutingService routingEngine;
	protected static Logger log = LoggerFactory.getLogger(LearningSwitch.class);
 
 
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "HeaderExtract";
	}
 
	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}
 
	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}
 
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
 
 
        return null;
	}
 
	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}
 
	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
		 l.add(IFloodlightProviderService.class);
	        l.add(IDeviceService.class);
	        l.add(IRoutingService.class);
	        l.add(ITopologyService.class);
	        l.add(ICounterStoreService.class);
		return l;
		// TODO Auto-generated method stub
	}
 
	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider =
                context.getServiceImpl(IFloodlightProviderService.class);
				this.deviceManager = context.getServiceImpl(IDeviceService.class);
				this.routingEngine = context.getServiceImpl(IRoutingService.class);
		        this.counterStore = context.getServiceImpl(ICounterStoreService.class);
		        messageDamper = new OFMessageDamper(10000,
                        EnumSet.of(OFType.FLOW_MOD),
                        250);
		// TODO Auto-generated method stub
 
	}
 
	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);
		// TODO Auto-generated method stub
	}
 
	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {

		switch (msg.getType()) {
        case PACKET_IN:
            //return this.processPacketInMessage(sw, (OFPacketIn) msg, cntx);
        default:
            break;
    }
		//uid/in_port/sw_dpid/time
		//header: mac/ip/port/protocol
		int Association_ID;
		String uid = "";//default is empty
		
		short in_port;
		long sw_dpid;
		
		String src_mac;
		String dst_mac;
		String src_ip;
		String dst_ip;
		short src_port;
		short dst_port;
		byte protocol;//Protocol Numbers: http://www.iana.org/assignments/protocol-numbers/protocol-numbers.xhtml
		java.sql.Timestamp time;
		//=============================================================
		java.util.Date current_time = new java.util.Date(); 
		long t = current_time.getTime();
		time = new java.sql.Timestamp(t);
		
		IDevice srcDevice = IDeviceService.fcStore.get(cntx, IDeviceService.CONTEXT_SRC_DEVICE);
	    SwitchPort[] srcDaps = srcDevice.getAttachmentPoints();//Get all unique attachment points associated with the device.
	    in_port = (short)srcDaps[0].getPort();
	    sw_dpid = srcDaps[0].getSwitchDPID();
	    /*int iSrcDaps = 0, iDstDaps = 0;

        while ((iSrcDaps < srcDaps.length) && (iDstDaps < dstDaps.length)) {
                SwitchPort srcDap = srcDaps[iSrcDaps];
                SwitchPort dstDap = dstDaps[iDstDaps];
        }*/
		
		OFPacketIn pin = (OFPacketIn) msg;
		OFMatch match = new OFMatch();
		match.loadFromPacket(pin.getPacketData(), pin.getInPort());
		
		Long src_mac_long = Ethernet.toLong(match.getDataLayerSource());
		Long dst_mac_long = Ethernet.toLong(match.getDataLayerDestination());
		src_mac = HexString.toHexString(src_mac_long);
		dst_mac = HexString.toHexString(dst_mac_long);
		//src_mac = match.getDataLayerSource().toString();
		//dst_mac = match.getDataLayerDestination().toString();
		
		src_ip = IPv4.fromIPv4Address(match.getNetworkSource());//String<-->int, IPv4.fromIPv4Address(), IPv4.toIPv4Address()
		dst_ip = IPv4.fromIPv4Address(match.getNetworkDestination());
		src_port = match.getTransportSource();
		dst_port = match.getTransportDestination();
		protocol = match.getNetworkProtocol();
		//=============================================================
		ResourceAdaptor DB_manipulate = new ResourceAdaptor();
		String latest_record = "SELECT * FROM `Association` where `src_mac`='"+ src_mac +"' ORDER BY `time` DESC LIMIT 1";//for `uid`
		String mac_idle_pass = "SELECT * FROM `Registered_mac` where `src_mac`='"+ src_mac +"'";//for `pass`
		
		//query Association table by scr_mac, and find the latest record, and copy uid into new record 
		Object [] result = DB_manipulate.SelectTable(latest_record);
		
		if(result[0] == null) ;//not auth
		else uid = (String)result[0];
		
		String total_fields = uid + Short.toString(in_port) + Long.toString(sw_dpid) + src_mac + dst_mac + 
				src_ip + dst_ip + Short.toString(src_port) + Short.toString(dst_port) + Byte.toString(protocol) + time.toString();

		Association_ID = total_fields.hashCode();//define an unique index
		DB_manipulate.insertTable(Association_ID, uid, in_port, sw_dpid, src_mac, dst_mac, src_ip, dst_ip, src_port, dst_port, protocol, time);
		
		//query Registered_mac table to check idle/pass of src_mac
		//deliver Association_ID of new record to Dispatcher
		result = DB_manipulate.SelectTable(mac_idle_pass);
		if(result[0] == null || (int)result[3] == 0) ;//not auth
		else if((int)result[3] == 1)
		{
			
		}
		//=============================================================
		//=============================================================
		//=============================================================
		/*Long sourceMACHash = Ethernet.toLong(match.getDataLayerDestination());
		System.out.println("$$$$$-Get the Destination IP Address-$$$$$"); 
		System.out.println(IPv4.fromIPv4Address(match.getNetworkDestination()));
		System.out.println("$$$$$-Mac Address Destination-$$$$$$");
		System.out.println(HexString.toHexString(sourceMACHash));*/
		return Command.CONTINUE;
		//return Command.STOP;
	}
}
