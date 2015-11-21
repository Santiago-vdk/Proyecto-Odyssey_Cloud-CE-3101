package odyssey.logic;

import javax.json.JsonArray;

import org.json.simple.JSONArray;

/**
 *
 * @author RafaelAngel
 */
public class Sessions {
	
    private SessionNode _head = null;
    private SessionNode _tail = null;
    private int _tam = 0;
    
    private static Sessions _singleton = new Sessions();
    
    /* A private Constructor prevents any other 
     * class from instantiating.
     */
    private Sessions(){ }
    
    /* Static 'instance' method */
    public static Sessions getInstance( ) {
       return _singleton;
    }

    public void createSession(String pUsername, String pToken, String pLoginTime, String pListeningTo) {
    	SessionNode tmp = new SessionNode(pUsername, pToken, pLoginTime, pListeningTo);

        if (_head == null) {
            _head = tmp;
            _tail = tmp;
        } else {
            _tail.setNext(tmp);
            tmp.setPrev(_tail);
            _tail = tmp;
        }
        _tam++;

    }
    
    /**
     *
     * @param ptoken
     */
    public void deleteSession(String pToken) {
    	SessionNode tmp = _head;
        while (tmp != null) {
            if (tmp.getSession().getToken().compareTo(pToken) == 0) {
                if (_tam == 1) {
                    _head = null;
                    _tail = null;
                    _tam = 0;
                } //se valida si es el primer elemento
                else if (tmp == _head) {
                    tmp.getNext().setPrev(null);
                    _head = tmp.getNext();
                    _tam--;
                } //se valida si es el ultimo elemento
                else if (tmp == _tail) {
                    tmp.getPrev().setNext(null);
                    _tail = tmp.getPrev();
                    _tam--;
                } else {
                    tmp.getPrev().setNext(tmp.getNext());
                    tmp.getNext().setPrev(tmp.getPrev());
                    _tam--;
                }
            }
            tmp = tmp.getNext();
        }
    }

    /**
     *
     * @param ptoken
     * @return
     */
    public SessionNode find(String pToken) {
    	SessionNode tmp = _head;

        while (tmp != null) {
            if (tmp.getSession().getToken().compareTo(pToken) == 0) {
                return tmp;
            }
            tmp = tmp.getNext();
        }
        return null;
    }
    

   public SessionNode findByUsername(String pUser) {
   	SessionNode tmp = _head;

       while (tmp != null) {
           if (tmp.getSession().getUser().compareTo(pUser) == 0) {
               return tmp;
           }
           tmp = tmp.getNext();
       }
       return null;
   }
   
   public JSONArray getOnlineSessions(){
	   
	   JSONArray lista = new JSONArray();
	   SessionNode tmp = _head;
	   while(tmp != null){
		   
		   lista.add(tmp.getSession().getUser());
		   tmp = tmp.getNext();
	   }
	   return lista;
	   
	   
   }

  
    /**
     * @return the _head
     */
    public SessionNode getHead() {
        return _head;
    }

    /**
     * @param pHead
     */
    public void setHead(SessionNode pHead) {
        _head = pHead;
    }

    /**
     * @return the _tail
     */
    public SessionNode getTail() {
        return _tail;
    }

    /**
     * @param pTail
     */
    public void setTail(SessionNode pTail) {
        _tail = pTail;
    }

    /**
     * @return the _tam
     */
    public int getTam() {
        return _tam;
    }

    /**
     * @param pTam
     */
    public void setTam(int pTam) {
        _tam = pTam;
    }
}
