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
			System.out.print("Terminate\t[-1]" + "\nAdd post\t[0]" + "\nAdd like\t[1]" + "\nInfluencers\t[2]"
					+ "\nMentioned\t[3]" + "\n>> ");
			String ln = read_in.nextLine();
			switch (Integer.parseInt(ln)) {
				case -1:
					System.out.println("Quitting");
					cont = false;
					break;
				case 0:
					new_post(net, read_in);
					break;
				case 1:
					new_like(net, read_in);
					break;
				case 2:
					display_influencers(net);
					break;
				case 3:
					display_mentioned(net);
					break;
			}

			// prints the modified social network
			System.out.println("***Current status***\n" + net.toString() + "********************\n");
		} while (cont);

		// print the social network

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

	private static void display_influencers(SocialNetwork net) {
		System.out.println("Influencers:\n" + net.influencers());
	}
	private static void display_mentioned(SocialNetwork net) {
		System.out.println("Mentioned users:\n" + net.getMentionedUsers());
	}
}