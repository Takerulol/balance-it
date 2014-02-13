package de.hsbremen.mobile.googleplay;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig.Builder;
import com.google.example.games.basegameutils.BaseGameActivity;

import de.hsbremen.mobile.balanceit.MainActivity;
import de.hsbremen.mobile.balanceit.gameservices.NetworkManager;


/**
 * 
 * @author Thorsten
 *
 * This class implements the RoomUpdateListener and RoomStatusUpdateListener interface.
 * It is used to encapsulate all code required to keep track of a room.
 */
public class RoomManager implements RoomUpdateListener, RoomStatusUpdateListener, RealTimeMessageReceivedListener {

	public interface Listener {
		void onStartMultiplayerGame(boolean firstPlayer);
	}
	
	// arbitrary request code for the waiting room UI.
	// This can be any integer that's unique in your Activity.
	public final static int RC_WAITING_ROOM = 10002;
	final static int REQUIRED_PLAYERS = 2;

	
	// are we already playing?
	private boolean mPlaying = false;
	private MainActivity activity;
	private GamesClient gamesClient;
	private String mRoomId = null;
	private String participantId = null;
	private Room mRoom = null;
	private NetworkManager networkManager = null;

	private static final String TAG = "RoomManager";

	private List<Listener> listenerList;
	
	public RoomManager(MainActivity activity, GamesClient gamesClient)
	{
		this.activity = activity;
		this.gamesClient = gamesClient;
		this.listenerList = new ArrayList<Listener>();
	}
	
	public void addListener(Listener listener) {
		this.listenerList.add(listener);
	}
	
	public void removeListener(Listener listener) {
		this.listenerList.remove(listener);
	}
	
	/**
	 * Notifies all listeners with the onStartMultiplayerGame method.
	 * @param host Determines whether or not the local player is the host.
	 */
	private void notifyListener() {
		boolean firstPlayer = isFirstPlayer();
		for (Listener lis : listenerList) {
			lis.onStartMultiplayerGame(firstPlayer);
		}
	}
	
	/**
	 * Determines whether or not the local player should be the first player.
	 * In order to do this, the random playerids are getting compared.
	 * In addition, the participantId gets set.
	 */
	public boolean isFirstPlayer() {
		String localId = mRoom.getParticipantId(gamesClient.getCurrentPlayerId());
		String remoteId = null;

		ArrayList<String> ids = mRoom.getParticipantIds();
		for(int i=0; i<ids.size(); i++)
		{
		    String test = ids.get(i);
		    if( !test.equals(localId))
		    {
		        remoteId = test;
		        break;
		    }
		}
		this.participantId = remoteId;
		return localId.compareTo(remoteId) > 0;
	}
	
	// returns whether there are enough players to start the game
	boolean shouldStartGame(Room room) {
	    int connectedPlayers = 0;
	    for (Participant p : room.getParticipants()) {
	        if (p.isConnectedToRoom()) ++connectedPlayers;
	    }
	    return connectedPlayers >= REQUIRED_PLAYERS;
	}

	// Returns whether the room is in a state where the game should be cancelled.
	boolean shouldCancelGame(Room room) {
	    // TODO: Your game-specific cancellation logic here. For example, you might decide to 
	    // cancel the game if enough people have declined the invitation or left the room.
	    // You can check a participant's status with Participant.getStatus().
	    // (Also, your UI should have a Cancel button that cancels the game too)
		
		return false;
	}

	@Override
	public void onPeersConnected(Room room, List<String> peers) {
		mRoom = room;
		Log.d(TAG, "onPeersConnected.");
	    if (mPlaying) {
	        // add new player to an ongoing game
	    }
	    else if (shouldStartGame(room)) {
	    	Log.d(TAG, "Notifing Listener.");
	        notifyListener();
	    }
	}

	@Override
	public void onPeersDisconnected(Room room, List<String> peers) {
	    if (mPlaying) {
	        // do game-specific handling of this -- remove player's avatar 
	        // from the screen, etc. If not enough players are left for
	        // the game to go on, end the game and leave the room.
	    }
	    else if (shouldCancelGame(room)) {
	        // cancel the game
	        gamesClient.leaveRoom(this, room.getRoomId());
	        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    }
	}

	@Override
	public void onPeerLeft(Room room, List<String> peers) {
	    // peer left -- see if game should be cancelled
	    if (!mPlaying && shouldCancelGame(room)) {
	        gamesClient.leaveRoom(this, room.getRoomId());
	        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    }
	}

	@Override
	public void onPeerDeclined(Room room, List<String> peers) {
	    // peer declined invitation -- see if game should be cancelled
	    if (!mPlaying && shouldCancelGame(room)) {
	    	gamesClient.leaveRoom(this, room.getRoomId());
	        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    }
	}

	
	
	@Override
	public void onConnectedToRoom(Room room) {
		Log.d(TAG, "onConnectedToRoom.");

        // get room ID, participants and my ID:
        mRoomId = room.getRoomId();
        mRoom = room;
        //mParticipants = room.getParticipants();
        //mMyId = room.getParticipantId(getGamesClient().getCurrentPlayerId());

        // print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + mRoomId);
        //Log.d(TAG, "My ID " + mMyId);
        Log.d(TAG, "<< CONNECTED TO ROOM>>");
	}

	@Override
	public void onDisconnectedFromRoom(Room arg0) {
		mRoomId = null;
		mRoom = null;
		//TODO show error and switch to main screen
		
	}

	@Override
	public void onP2PConnected(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PDisconnected(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerInvitedToRoom(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeerJoined(Room arg0, List<String> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomAutoMatching(Room arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRoomConnecting(Room arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onLeftRoom(int arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onRoomCreated(int statusCode, Room room) {
	    if (statusCode != GamesClient.STATUS_OK) {
	        // let screen go to sleep
	        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	        // show error message, return to main screen.
	    }
	    
	    else
	    {
	    	// get waiting room intent
	    	mRoom = room;
	        Intent i = gamesClient.getRealTimeWaitingRoomIntent(room, REQUIRED_PLAYERS);
	        activity.startActivityForResult(i, RC_WAITING_ROOM);

	    }
	}

	@Override
	public void onJoinedRoom(int statusCode, Room room) {
	    if (statusCode != GamesClient.STATUS_OK) {
	        // let screen go to sleep
	        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	        // show error message, return to main screen.
	    }
	    
	    else
	    {
	    	// get waiting room intent
	    	mRoom = room;
	        Intent i = gamesClient.getRealTimeWaitingRoomIntent(room, REQUIRED_PLAYERS);
	        activity.startActivityForResult(i, RC_WAITING_ROOM);

	    }
	}

	@Override
	public void onRoomConnected(int statusCode, Room room) {
	    if (statusCode != GamesClient.STATUS_OK) {
	        // let screen go to sleep
	        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	        // show error message, return to main screen.
	    }
	    else {
	    	mRoom = room;
	    }
	}
	
	/**
	 * Leaves the room and returns to the main screen
	 */
    public void leaveRoom() {
        Log.d(TAG, "Leaving room.");
//        mSecondsLeft = 0;
//        stopKeepingScreenOn();
        if (mRoomId != null) {
            gamesClient.leaveRoom(this, mRoomId);
            mRoomId = null;
            mRoom = null;
//            switchToScreen(R.id.screen_wait);
//        } else {
//            switchToMainScreen();
        }
    }
    
    /**
     * Accepts the invitation and joins the room.
     * @param InvitationId
     */
    public void acceptInvitation(String InvitationId)
    {
    	Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setInvitationIdToAccept(InvitationId);
        gamesClient.joinRoom(roomConfigBuilder.build());

        // prevent screen from sleeping during handshake
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        //notifyListener();
    }
    
    private Builder makeBasicRoomConfigBuilder() {
		RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        
        return rtmConfigBuilder;
	}
    
    public void startQuickGame() {
	    // automatch criteria to invite 1 random automatch opponent.  
	    // You can also specify more opponents (up to 3). 
    	int minMaxPlayers = REQUIRED_PLAYERS - 1; 
	    Bundle am = RoomConfig.createAutoMatchCriteria(minMaxPlayers, minMaxPlayers, 0);

	    // build the room config:
	    RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
	    roomConfigBuilder.setAutoMatchCriteria(am);
	    RoomConfig roomConfig = roomConfigBuilder.build();

	    // create room:
	    gamesClient.createRoom(roomConfig);

	    // prevent screen from sleeping during handshake
	    //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	    //notifyListener();
	}
    
    public void handleAutoMatching(ArrayList<String> invitees) {
        // create the room and specify a variant if appropriate
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.addPlayersToInvite(invitees);
        RoomConfig roomConfig = roomConfigBuilder.build();
        gamesClient.createRoom(roomConfig);

        // prevent screen from sleeping during handshake
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    
    /**
     * Returns the currently active RoomId.
     * null, if no room is active at the moment.
     */
    public String getRoomId() {
    	return this.mRoomId;
    }
    
    /**
     * Returns the participantId of the remote player.
     */
    public String getParticipantId() {
    	return participantId;
    }
    
    public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage arg0) {
		if (this.networkManager != null) 
		{
			this.networkManager.onRealTimeMessageReceived(arg0.getMessageData());
		}
		
	}
    
	
}
