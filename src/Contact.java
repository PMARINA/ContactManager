import java.util.Comparator;

/**
 * I learned lambda functions!!! It's just a wrapper for a string and a long,
 * nothing really to special though
 * 
 * @author PMARINA
 * @version 4/21/2017
 *
 */
public class Contact {
	public String s;
	public long t;

	public Contact(String s, long l) {
		this.s = s;
		this.t = l;
	}

	public static Comparator<Contact> getComparator() {
		return (arg0, arg1) -> ((Contact) arg0).s.compareTo(((Contact) arg1).s);
	}

	public static Comparator<Contact> getAutocomplete() {
		return (arg0, arg1) -> {
			if (((Contact) arg0).s.contains(((Contact) arg1).s))
				return 0;
			else
				return -1;
		};
	}
}
