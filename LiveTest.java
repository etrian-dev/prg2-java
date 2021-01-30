import java.util.Date;
import java.util.Scanner;

public class LiveTest {
	public static void main(String[] args) {
		// an empty social network
		SocialNetwork net = new SocialNetwork();

		Scanner read_in = new Scanner(System.in);
		// add posts until the user types "fine"
		boolean cont = true;
		do {
			System.out.print("\nAdd post\t[0]\nAdd like\t[1]\nTerminate\t[2]\n>> ");
			String ln = read_in.nextLine();
			if(ln.equals("0")) {
				new_post(net, read_in);
			}
			else if(ln.equals("1")) {
				new_like(net, read_in);
			}
			else {
				System.out.println("Quitting");
				cont = false;
			}

			// prints the modified social network
			System.out.println("Modified Social network:\n" + net.toString());
		} while(cont);

		read_in.close();
		
		// print the social network
		System.out.println("Social network:\n" + net.toString());
		System.out.println(
				"Influencers: " + net.influencers().toString() + "\nMentioned users: " + net.getMentionedUsers());
	}
	private static void new_post(SocialNetwork net, Scanner s) {
		System.out.println("Insert author: ");
		String auth = s.nextLine();
		System.out.println("Insert text: ");
		String text = s.nextLine();
		try {
			// creates an instance with the current UNIX time converted to a date
			Post ps = new Post(auth, text, new Date(System.currentTimeMillis()));
			// tries to add it to the social network
			net.addPost(ps);
		} catch (Exception e) {
			System.out.println("Cannot generate the post: caught exception\n\t" + e);
		}
	}
	private static void new_like(SocialNetwork net, Scanner s) {
		System.out.println("Insert pid: ");
		String pidstr = s.nextLine();
		Integer pid = Integer.parseInt(pidstr);
		System.out.println("Insert liker: ");
		String liker = s.nextLine();
		// try to add like to the post
		try {
			net.likePost(pid, liker);

		} catch (Exception e) {
			System.out.println("Cannot add like the post with pid " + pid + ": caught exception\n\t" + e);
		}
	}
}