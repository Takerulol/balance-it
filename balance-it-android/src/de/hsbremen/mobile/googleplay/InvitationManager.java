package de.hsbremen.mobile.googleplay;

import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;

/**
 * This class handles invitations.
 * @author Thorsten
 *
 */
public class InvitationManager implements OnInvitationReceivedListener {

	private RoomManager roomManager;
	private GamesClient gamesClient;

	public InvitationManager(GamesClient client, RoomManager roomManager)
	{
		this.gamesClient = client;
		this.gamesClient.registerInvitationListener(this);
		
		this.roomManager = roomManager;
	}
	
	@Override
	public void onInvitationReceived(Invitation invitation) {
		// TODO Used to accept invitations during gameplay
		
		// show in-game popup to let user know of pending invitation

	    // store invitation for use when player accepts this invitation
	    //mIncomingInvitationId = invitation.getInvitationId();

		//--> roomManager.acceptInvitation(mInomingInvitationId);
		//--> when the user accepts the popup invitation shown earlier.
	}
	
	/**
	 * Handles the invitation. If the invitationId is not null, 
	 * the invitation will be accepted. After that the method will 
	 * join the game room.
	 */
	public void handleInvitation(String InvitationId) {
		if (InvitationId != null)
		{
			this.roomManager.acceptInvitation(InvitationId);
		}
	}

	
}
