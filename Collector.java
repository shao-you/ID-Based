package net.floodlightcontroller.headerextract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFType;
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
import net.floodlightcontroller.topology.ITopologyService;


public class HeaderExtract implements IOFMessageListener, IFloodlightModule {
 
	public final int DEFAULT_CACHE_SIZE = 10;
	protected IFloodlightProviderService floodlightProvider;

	protected static Logger log = LoggerFactory.getLogger(LearningSwitch.class);
	ResourceAdaptor DB_manipulate;
 
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
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		// TODO Auto-generated method stub
 
	}
 
	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);//註冊成為其他Service的Listener
		DB_manipulate = new ResourceAdaptor();
		// TODO Auto-generated method stub
	}
 
	@Override
	public net.floodlightcontroller.core.IListener.Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) 
	{
		java.util.Date current_time = new java.util.Date(); 
		long t = current_time.getTime();
		
		int Association_ID;
		String uid = "";//default is empty
		short in_port=0;//
		long sw_dpid=0;//
		String src_mac;
		String dst_mac;
		String src_ip;
		String dst_ip;
		int src_port;
		int dst_port;
		byte protocol;//Protocol Numbers: http://www.iana.org/assignments/protocol-numbers/protocol-numbers.xhtml
		java.sql.Timestamp time;
		
		OFPacketIn pin = (OFPacketIn) msg;
		OFMatch match = new OFMatch();
		match.loadFromPacket(pin.getPacketData(), pin.getInPort());
		
		src_port = match.getTransportSource()  & 0x0000ffff;//from singned short to unsigned value (by int)
		dst_port = match.getTransportDestination()  & 0x0000ffff;
		protocol = match.getNetworkProtocol();
		
		if(match.getDataLayerType() == (short)0x0800 && //IPv4, floodlight not support IPv6
				!(src_port==68 && dst_port==67 && protocol==17)) //not DHCP
		{
			switch (msg.getType()) 
			{
	        	case PACKET_IN:
		
				time = new java.sql.Timestamp(t);
				IDevice srcDevice = IDeviceService.fcStore.get(cntx, IDeviceService.CONTEXT_SRC_DEVICE);
			    SwitchPort[] srcDaps = srcDevice.getAttachmentPoints();//Get all unique attachment points associated with the device.
			    if(srcDaps.length==0) System.out.println("=========================");
			    else in_port = (short)srcDaps[0].getPort();
			    sw_dpid = srcDaps[0].getSwitchDPID();
			    /*int iSrcDaps = 0, iDstDaps = 0;
		        while ((iSrcDaps < srcDaps.length) && (iDstDaps < dstDaps.length)) {
		                SwitchPort srcDap = srcDaps[iSrcDaps];
		                SwitchPort dstDap = dstDaps[iDstDaps];
		        }*/
				
				Long src_mac_long = Ethernet.toLong(match.getDataLayerSource());
				Long dst_mac_long = Ethernet.toLong(match.getDataLayerDestination());
				src_mac = HexString.toHexString(src_mac_long);
				dst_mac = HexString.toHexString(dst_mac_long);
				//src_mac = match.getDataLayerSource().toString();
				//dst_mac = match.getDataLayerDestination().toString();
				
				src_ip = IPv4.fromIPv4Address(match.getNetworkSource());//String<-->int, IPv4.fromIPv4Address(), IPv4.toIPv4Address()
				dst_ip = IPv4.fromIPv4Address(match.getNetworkDestination());
		
				//=============================================================
				if(DB_manipulate.getConState()) {System.out.println("FAILED CONNECTION!!");return Command.CONTINUE;}
				else System.out.println("SUCCESSFUL CONNECTION!!");
				
				String latest_record = "SELECT * FROM `Association` where `src_mac`='"+ src_mac +"' ORDER BY `time` DESC LIMIT 1";//for `uid`
				String mac_idle_pass = "SELECT * FROM `Registered_mac` where `src_mac`='"+ src_mac +"'";//for `pass`
				
				//query Association table by scr_mac, and find the latest record, and copy uid into new record 
				
				Object [] result = DB_manipulate.SelectTable(latest_record);
					
				if(result[1] == null || result[1] == "") ;//not auth
				else uid = (String)result[1];
				
				String total_fields = uid + Short.toString(in_port) + Long.toString(sw_dpid) + src_mac + dst_mac + 
						src_ip + dst_ip + Integer.toString(src_port) + Integer.toString(dst_port) + Byte.toString(protocol) + time.toString() +
						Integer.toString(new Random().nextInt());
				
				Association_ID = total_fields.hashCode();//define an unique index
				DB_manipulate.insertTable(Association_ID, uid, in_port, sw_dpid, src_mac, dst_mac, src_ip, dst_ip, src_port, dst_port, protocol, time);
				
				System.out.println(Association_ID+"  "+uid+"  "+in_port+"  "+sw_dpid+"  "+src_mac+"  "+dst_mac+"  "+src_ip+"  "+dst_ip+"  "+src_port+"  "+dst_port+"  "+protocol+"  "+time);
				//query Registered_mac table to check idle/pass of src_mac
				/*result = DB_manipulate.SelectTable(mac_idle_pass);
				if(result[0] == null || (int)result[3] == 0) ;//not auth
				else if((int)result[3] == 1)//deliver Association_ID of new record to Dispatcher
				{
					new SocketClient(Association_ID);
				}*/
				//=============================================================
				//=============================================================
				//=============================================================
				/*Long sourceMACHash = Ethernet.toLong(match.getDataLayerDestination());
				System.out.println("$$$$$-Get the Destination IP Address-$$$$$"); 
				System.out.println(IPv4.fromIPv4Address(match.getNetworkDestination()));
				System.out.println("$$$$$-Mac Address Destination-$$$$$$");
				System.out.println(HexString.toHexString(sourceMACHash));*/
				
		        default:
		            break;
			}
		}
		return Command.CONTINUE;
		//return Command.STOP;
	}
}
