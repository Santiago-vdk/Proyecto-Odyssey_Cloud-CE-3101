package odyssey.logic;

public class SessionNode {
	private SessionNode _next = null;
	private SessionNode _prev = null;
	private SessionObject _session = null;

	public SessionNode(String pUsername, String pToken) {
		_session = new SessionObject(pUsername, pToken);
	}
	
	public SessionObject getSession(){
		return _session;
	}

	/**
	 * @return the _next
	 */
	public SessionNode getNext() {
		return _next;
	}

	/**
	 * @param pNext
	 *            the _next to set
	 */
	public void setNext(SessionNode pNext) {
		_next = pNext;
	}

	/**
	 * @return the _prev
	 */
	public SessionNode getPrev() {
		return _prev;
	}

	/**
	 * @param pPrev
	 *            the _prev to set
	 */
	public void setPrev(SessionNode pPrev) {
		_prev = pPrev;
	}

}
