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
		
		//uid/time/dpid/in_port
		//header:ip/mac/port/protocol
		String uid;
		short in_port;
		
		
		
		OFPacketIn pin = (OFPacketIn) msg;
		OFMatch match = new OFMatch();
		match.loadFromPacket(pin.getPacketData(), pin.getInPort());

		/*Long sourceMACHash = Ethernet.toLong(match.getDataLayerDestination());
		System.out.println("$$$$$-Get the Destination IP Address-$$$$$"); 
		System.out.println(IPv4.fromIPv4Address(match.getNetworkDestination()));
		System.out.println("$$$$$-Mac Address Destination-$$$$$$");
		System.out.println(HexString.toHexString(sourceMACHash));*/
 
		Integer ipaddr = IPv4.toIPv4Address("10.0.0.1");
		Integer broadcast = IPv4.toIPv4Address("255.255.255.255");
		String mac = "ff:ff:ff:ff:ff:ff";
 
		if (match.getNetworkProtocol()==IPv4.PROTOCOL_UDP 
				&& (match.getNetworkSource())!=ipaddr 
				&& (match.getNetworkDestination())!=ipaddr 
				&& (match.getNetworkDestination())!=broadcast 
				&& match.getDataLayerDestination()!=Ethernet.toMACAddress(mac))
		{
			Iterator<? extends IDevice> dstiter = deviceManager.queryDevices(null, null, ipaddr, null, null);
			IDevice dvc = null;
			dvc = (IDevice) dstiter.next();
			
			IDevice srcDevice = IDeviceService.fcStore.get(cntx, IDeviceService.CONTEXT_SRC_DEVICE);
			//IDevice dstDevice = IDeviceService.fcStore.get(cntx, IDeviceService.CONTEXT_DST_DEVICE);
			SwitchPort[] srcDaps = srcDevice.getAttachmentPoints();
			SwitchPort[] dstDaps = dvc.getAttachmentPoints();//dstDevice.getAttachmentPoints();
	
			Route route = 
					routingEngine.getRoute(srcDaps[0].getSwitchDPID(), (short)srcDaps[0].getPort(),
							dstDaps[0].getSwitchDPID(), (short)dstDaps[0].getPort(), 0); 
	
			match.setNetworkProtocol(IPv4.PROTOCOL_UDP);
			match.setDataLayerType(Ethernet.TYPE_IPv4);
			//match.setTransportSource(match.getTransportSource());
			//match.setNetworkDestination(match.getNetworkDestination());
			match.setNetworkSource(match.getNetworkSource());
			match.setWildcards(~(OFMatch.OFPFW_NW_PROTO
					| OFMatch.OFPFW_DL_TYPE 
					//| OFMatch.OFPFW_TP_SRC 
					//| OFMatch.OFPFW_NW_DST_MASK
					| OFMatch.OFPFW_NW_SRC_MASK)); 
	
			OFFlowMod mod = (OFFlowMod) floodlightProvider.getOFMessageFactory().getMessage(OFType.FLOW_MOD);
			mod.setMatch(match);
			mod.setCommand(OFFlowMod.OFPFC_ADD);
			mod.setIdleTimeout((short)0);
			mod.setHardTimeout((short)0);
			mod.setPriority((short)(5566));//Short.MAX_VALUE - 1
			mod.setBufferId(OFPacketOut.BUFFER_ID_NONE);
			mod.setFlags((short)(1 << 0));
	
			String mac_10_0_0_1 = dvc.getMACAddressString();
			List<OFAction> actions = new ArrayList<OFAction>();
			actions.add(new OFActionDataLayerDestination(Ethernet.toMACAddress(mac_10_0_0_1)));
			actions.add(new OFActionNetworkLayerDestination(IPv4.toIPv4Address("10.0.0.1")));
			actions.add(new OFActionTransportLayerDestination((short)5134));
			short first_DPID = route.getPath().get(1).getPortId();
			actions.add(new OFActionOutput(first_DPID,(short)0xFFFF));
	
			mod.setActions(actions);
			mod.setLengthU(OFFlowMod.MINIMUM_LENGTH 
					+ OFActionNetworkLayerDestination.MINIMUM_LENGTH 
					+ OFActionDataLayerDestination.MINIMUM_LENGTH
					+ OFActionTransportLayerDestination.MINIMUM_LENGTH
					+ OFActionOutput.MINIMUM_LENGTH);
	
			try {//set a rule  in the first switch for modifying the header
				sw.write(mod, cntx); 
				sw.flush(); 
			} catch (IOException e) { 
					e.printStackTrace(); 
				} 
			
			OFMatch match2 = new OFMatch();
			match2.setNetworkProtocol(IPv4.PROTOCOL_UDP);
			match2.setDataLayerType(Ethernet.TYPE_IPv4);
			match2.setTransportDestination((short)5134);
			match2.setNetworkDestination(IPv4.toIPv4Address("10.0.0.1"));
			match2.setWildcards(~(OFMatch.OFPFW_NW_PROTO
					| OFMatch.OFPFW_DL_TYPE 
					| OFMatch.OFPFW_TP_DST
					| OFMatch.OFPFW_NW_DST_MASK)); 
	
			OFFlowMod mod2 = (OFFlowMod) floodlightProvider.getOFMessageFactory().getMessage(OFType.FLOW_MOD);
			mod2.setMatch(match2);
			mod2.setCommand(OFFlowMod.OFPFC_ADD);
			mod2.setIdleTimeout((short)0);
			mod2.setHardTimeout((short)0);
			mod2.setPriority((short)(Short.MAX_VALUE - 1));
			mod2.setBufferId(OFPacketOut.BUFFER_ID_NONE);
			mod2.setFlags((short)(1 << 0));
			mod2.setLengthU(OFFlowMod.MINIMUM_LENGTH 
					+ OFActionOutput.MINIMUM_LENGTH);
			
			OFMatch match3 = new OFMatch();
			match3.setNetworkProtocol(IPv4.PROTOCOL_UDP);
			match3.setDataLayerType(Ethernet.TYPE_IPv4);
			match3.setTransportDestination(match.getTransportSource());
			match3.setNetworkDestination(match.getNetworkSource());
			match3.setWildcards(~(OFMatch.OFPFW_NW_PROTO
					| OFMatch.OFPFW_DL_TYPE 
					| OFMatch.OFPFW_TP_DST
					| OFMatch.OFPFW_NW_DST_MASK)); 
	
			OFFlowMod mod3 = (OFFlowMod) floodlightProvider.getOFMessageFactory().getMessage(OFType.FLOW_MOD);
			mod3.setMatch(match3);
			mod3.setCommand(OFFlowMod.OFPFC_ADD);
			mod3.setIdleTimeout((short)0);
			mod3.setHardTimeout((short)0);
			mod3.setPriority((short)(Short.MAX_VALUE - 1));
			mod3.setBufferId(OFPacketOut.BUFFER_ID_NONE);
			mod3.setFlags((short)(1 << 0));
			mod3.setLengthU(OFFlowMod.MINIMUM_LENGTH 
					+ OFActionOutput.MINIMUM_LENGTH);
			
			List<OFAction> actions2 = new ArrayList<OFAction>();
			List<OFAction> actions3 = new ArrayList<OFAction>();
			List<OFMessage> messages = new ArrayList<OFMessage>();
			int route_nodes =  route.getPath().size();

			pushPacket(sw, match, pin, first_DPID, mac_10_0_0_1);//pkt_out the first pkt buffered in the first switch
			return Command.STOP;
		}
 
		return Command.CONTINUE;
		//return Command.STOP;
		// TODO Auto-generated method stub
	}
 
public void writeOFMessagesToSwitch(long dpid, List<OFMessage> messages) {
    	IOFSwitch ofswitch = (IOFSwitch) floodlightProvider.getSwitch(dpid);

        if (ofswitch != null) {  // is the switch connected
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Sending {} new entries to {}", messages.size(), dpid);
                }
                ofswitch.write(messages, null);
                ofswitch.flush();
            } catch (IOException e) {
                log.error("Tried to write to switch {} but got {}", dpid, e.getMessage());
            }
        }

    }
private void pushPacket(IOFSwitch sw, OFMatch match, OFPacketIn pi, short outport, String mac_10_0_0_1) {
    if (pi == null) {
        return;
    }

    // The assumption here is (sw) is the switch that generated the
    // packet-in. If the input port is the same as output port, then
    // the packet-out should be ignored.
    if (pi.getInPort() == outport) {
        if (log.isDebugEnabled()) {
            log.debug("Attempting to do packet-out to the same " +
                      "interface as packet-in. Dropping packet. " +
                      " SrcSwitch={}, match = {}, pi={}",
                      new Object[]{sw, match, pi});
            return;
        }
    }

    if (log.isTraceEnabled()) {
        log.trace("PacketOut srcSwitch={} match={} pi={}",
                  new Object[] {sw, match, pi});
    }

    OFPacketOut po =
            (OFPacketOut) floodlightProvider.getOFMessageFactory()
                                            .getMessage(OFType.PACKET_OUT);

    // set actions
    List<OFAction> actions = new ArrayList<OFAction>();
    actions.add(new OFActionOutput(outport, (short) 0xffff));
	actions.add(new OFActionDataLayerDestination(Ethernet.toMACAddress(mac_10_0_0_1)));
	actions.add(new OFActionNetworkLayerDestination(IPv4.toIPv4Address("10.0.0.1")));
	actions.add(new OFActionTransportLayerDestination((short)5134));
	int length = OFActionOutput.MINIMUM_LENGTH            		
	        + OFActionNetworkLayerDestination.MINIMUM_LENGTH 
            + OFActionDataLayerDestination.MINIMUM_LENGTH
            + OFActionTransportLayerDestination.MINIMUM_LENGTH;
    po.setActions(actions)
      .setActionsLength((short)length);
    short poLength =
            (short) (po.getActionsLength() + OFPacketOut.MINIMUM_LENGTH);

    // If the switch doens't support buffering set the buffer id to be none
    // otherwise it'll be the the buffer id of the PacketIn
    if (sw.getBuffers() == 0) {
        // We set the PI buffer id here so we don't have to check again below
        pi.setBufferId(OFPacketOut.BUFFER_ID_NONE);
        po.setBufferId(OFPacketOut.BUFFER_ID_NONE);
    } else {
        po.setBufferId(pi.getBufferId());
    }

    po.setInPort(pi.getInPort());

    // If the buffer id is none or the switch doesn's support buffering
    // we send the data with the packet out
    if (pi.getBufferId() == OFPacketOut.BUFFER_ID_NONE) {
        byte[] packetData = pi.getPacketData();
        poLength += packetData.length;
        po.setPacketData(packetData);
    }

    po.setLength(poLength);

    try {
        counterStore.updatePktOutFMCounterStoreLocal(sw, po);
        sw.write(po, null);
    } catch (IOException e) {
        log.error("Failure writing packet out", e);
    }
}
 
}
