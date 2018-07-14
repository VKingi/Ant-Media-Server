package io.antmedia.webrtc.adaptor;

import java.io.UnsupportedEncodingException;

import javax.websocket.Session;

import org.json.simple.JSONObject;
import org.red5.logging.Red5LoggerFactory;
import org.red5.net.websocket.WebSocketConnection;
import org.slf4j.Logger;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnection.Observer;
import org.webrtc.PeerConnection.SignalingState;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

import io.antmedia.websocket.WebSocketCommunityHandler;

public abstract class Adaptor implements Observer, SdpObserver 
{
	protected PeerConnection peerConnection;
	private MediaConstraints sdpMediaConstraints;
	protected PeerConnectionFactory peerConnectionFactory;
	
	private String streamId;
	
	private Session session; 
	
	protected static final Logger log = Red5LoggerFactory.getLogger(Adaptor.class);

	
	public abstract void start();
	
	public abstract void stop();

	public void setPeerConnection(PeerConnection peerConnection) {
		this.peerConnection = peerConnection;
	}
	
	public PeerConnection getPeerConnection() {
		return peerConnection;
	}

	@Override
	public void onSignalingChange(SignalingState newState) {

	}

	@Override
	public void onIceConnectionChange(IceConnectionState newState) {
		if (newState == IceConnectionState.DISCONNECTED || newState == IceConnectionState.FAILED
				|| newState == IceConnectionState.CLOSED) 
		{
			stop();
		}
	}

	@Override
	public void onIceConnectionReceivingChange(boolean receiving) {

	}

	@Override
	public void onIceGatheringChange(IceGatheringState newState) {

	}
	
	@Override
	public void onIceCandidate(IceCandidate candidate) {
		log.warn("onIceCandidate");
		
		WebSocketCommunityHandler.sendTakeCandidateMessage(candidate.sdpMLineIndex, candidate.sdpMid, candidate.sdp, streamId, session);

	}
	
	@Override
	public void onIceCandidatesRemoved(IceCandidate[] candidates) {
	}
	
	public void onAddStream(MediaStream stream) {}
	
	@Override
	public void onRemoveStream(MediaStream stream) {

	}

	@Override
	public void onDataChannel(DataChannel dataChannel) {

	}

	@Override
	public void onRenegotiationNeeded() {

	}

	@Override
	public void onAddTrack(RtpReceiver receiver, MediaStream[] mediaStreams) {
	}
	
	@Override
	public void onCreateSuccess(SessionDescription sdp) {
		log.warn("onCreate Success");
		peerConnection.setLocalDescription(new SdpObserver() {

			@Override
			public void onSetSuccess() {

			}

			@Override
			public void onSetFailure(String error) {

			}

			@Override
			public void onCreateSuccess(SessionDescription sdp) {

			}

			@Override
			public void onCreateFailure(String error) {

			}
		}, sdp);

		
		String type;
		if (sdp.type == Type.ANSWER) {
			type = "answer";
		}
		else  {
			type = "offer";
		}
		
		WebSocketCommunityHandler.sendSDPConfiguration(sdp.description, type, streamId, session);

	}
	
	@Override
	public void onSetSuccess() {
		log.warn("on setSuccess");
	}

	@Override
	public void onCreateFailure(String error) {
		log.warn(error);
	}

	@Override
	public void onSetFailure(String error) {
		log.warn(error);
	}

	public MediaConstraints getSdpMediaConstraints() {
		return sdpMediaConstraints;
	}

	public void setSdpMediaConstraints(MediaConstraints sdpMediaConstraints) {
		this.sdpMediaConstraints = sdpMediaConstraints;
	}

	public void setPeerConnectionFactory(PeerConnectionFactory peerConnectionFactory) {
		this.peerConnectionFactory = peerConnectionFactory;	
	}
	

	public void setSession(Session session) {
		this.session = session;
	}
	
	public Session getSession() {
		return session;
	}

	public String getStreamId() {
		return streamId;
	}

	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}

}