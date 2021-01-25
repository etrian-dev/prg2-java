import java.util.Date;
import java.util.Scanner;

public class LiveTest {
	public static void main(String[] args) {
		// an empty social network
		SocialNetwork net = new SocialNetwork();

		Scanner read_in = new Scanner(System.in);
		// add posts until the user types "fine"
		String auth, text, ln;
		Post ps;
		do {
			System.out.println("Insert author: ");
			auth = read_in.nextLine();
			System.out.println("Insert text: ");
			text = read_in.nextLine();
			try {
				// creates an instance with the current UNIX time converted to a date
				ps = new Post(auth, text, new Date(System.currentTimeMillis()));
				// tries to add it to the social network
				net.addPost(ps);
			} catch (Exception e) {
				System.out.println("Cannot generate the post: caught exception\n\t" + e);
			}
			ln = read_in.nextLine();

			// prints the modified social network
			System.out.println("Modified Social network:\n" + net.toString());
		} while (!ln.equals("fine"));
		// throws away the line containing "fine"
		read_in.nextLine();

		// posts added: print the social network
		System.out.println("Posts added.\nSocial network:\n" + net.toString());

		// add likes to posts
		Integer pid;
		String dummy;
		do {
			System.out.println("Insert pid: ");
			dummy = read_in.nextLine();
			pid = Integer.parseInt(dummy);
			System.out.println("Insert liker: ");
			dummy = read_in.nextLine();
			// try to add like to the post
			try {
				net.likePost(pid, dummy);

			} catch (Exception e) {
				System.out.println("Cannot add like the post with pid " + pid + ": caught exception\n\t" + e);
			}
			ln = read_in.nextLine();

			// prints the modified social network
			System.out.println("Modified Social network:\n" + net.toString());
		} while (!ln.equals("fine"));

		read_in.close();

		// likes added: print the social network
		System.out.println("Likes added.\nSocial network:\n" + net.toString());

		System.out.println(
				"Influencers: " + net.influencers().toString() + "\nMentioned users: " + net.getMentionedUsers());
	}
}