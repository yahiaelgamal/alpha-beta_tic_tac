import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;
import java.util.Vector;

public class Grid implements Serializable
{
	
	private static final long	serialVersionUID	= 2L;

	public enum Val{
		X((byte)1), 
		O((byte)-1), 
		NONE((byte)0),
		TIE((byte)0);
		
		byte num;
		
		private Val(byte n){
			num = n;
		}
	}
	
	public Val[][] grid;
	public Val turn = Val.X;
	
	static Val computer = Val.X;
	static Val human = Val.O;
	
	public Grid(){
		grid = new Val[3][3];
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid.length; j++)
				grid[i][j] = Val.NONE;
	}
	
	public Grid(Grid copy){
		grid = new Val[copy.grid.length][copy.grid.length];
		for (int i = 0; i < copy.grid.length; i++)
			for (int j = 0; j < copy.grid[i].length; j++)
				grid[i][j] = copy.grid[i][j];
				
		turn = copy.turn;
	}
	
	public Grid(int width){
		grid = new Val[width][width];
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid.length; j++)
				grid[i][j] = Val.NONE;
	}
	
	public boolean nextTurn(int sq){
		sq = sq-1;
		int x = sq/grid.length;
		int y = sq%grid.length;
		return nextTurn(x,y);
	}
	
	public static int[] convert(int sq){
		return new int[]{sq/HEAD.g.grid.length, sq%HEAD.g.grid.length};
	}
	public static int convert(int x, int y){
		return HEAD.g.grid.length * x + y;
	}
	
	
	public boolean nextTurn(int x, int y){
		
		if (grid[x][y] == Val.NONE)
		{
			grid[x][y] = turn;
			turn = turn == Val.X? Val.O:Val.X;
			return true;
		}
		
		return false;
	}
	
	public Val isFinished(){
		
		Val r1 = checkHor();
		Val r2 = checkVer();
		Val r3 = checkDia();
		Val[] r = {r1,r2,r3};
		for (int i = 0; i < r.length; i++){
			if (r[i]!= Val.NONE)
				return r[i];
		}
		
		boolean full = true;
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				if (grid[i][j] == Val.NONE)
				{
					full = false;
					break;
				}
		
		if (full)
			return Val.TIE;

		return Val.NONE;
	}
	
	public Val checkHor(){
		for (int i = 0; i < grid.length; i++){
			Val player = grid[i][0];
			int j = 0;
			for (j = 1; j < grid[i].length; j++){
				if (grid[i][j] != player){
					break;
				}
			}
			if (j == grid[i].length)
				return player;
		}
		return Val.NONE;
	}
	
	public Val checkVer(){
		for (int i = 0; i < grid[0].length; i++){
			Val player = grid[0][i];
			int j = 0;
			for ( j = 1; j < grid.length; j++){
				if (grid[j][i] != player)
					break;
			}
			if (j == grid.length)
				return player;
		}
		return Val.NONE;
	}
	
	public Val checkDia(){
		Val player1 = grid[0][0];
		int i = 0;
		for (i = 0; i < grid.length; i++)
			if (grid[i][i] != player1)
				break;
		
		if (i == grid.length)
			return player1;
		
		player1 = grid[0][grid.length-1];
		i = 0;
		for (i = 0; i < grid.length; i++)
			if (grid[grid.length-1-i][i] != player1)
				break; 
		
		if (i == grid.length)
			return player1;
		
		return Val.NONE;
	}
	
	public String toString(){
		String seg = "";
		int maxSeg = grid.length * grid.length;
		for (int i = 0; i < (maxSeg + "").length(); i++)
			seg += "-";
		
		String row = "";
		
		for (int i = 0; i < grid.length; i++)
			row += "+" + seg;
		row += '+';
		
		String res = row + "\n";
		
		for (int i = 0; i < grid.length; i++){
			res += "|";
			for (int j = 0; j < grid[i].length; j++)
//				if (grid[i][j] != Val.NONE)
//					res += " " + grid[i][j]  + " |";
//				else
//					res += " " + (i *grid.length + j + 1) + " |";
				if (grid[i][j] != Val.NONE)
					res += String.format("%s" , grid[i][j]) + "|";
				else{
					res += String.format("%s" , (i *grid.length + j + 1) ) + "|";
				}
				
			
			res += "\n";
			res += row +"\n";
		}
		
		return res;
	}
	
	private static void spinning() throws InterruptedException {
		
		char[] cs = {'\\', '|', '/'};
		for (int i = 0; i < cs.length; i++){
			System.out.flush();
			System.out.print("\b\b" + cs[i] + " ");
			Thread.sleep(500);
		}
		System.out.println("\b\b" + "  ");
		System.out.flush();
	}
	
	public Node[] genAllNodes(){
		int num = 0;
		for (int i = 0; i < this.grid.length; i++)
			for (int j = 0; j < this.grid.length; j++)
				if (this.grid[i][j] == Val.NONE)
					num++;
		int offset = 0;
		Node[] vec = new Node[num];
		for (int i = 0; i < grid.length; i++)
		{
			for (int j = 0; j < grid[i].length; j++) 
			{
				if (grid[i][j] == Val.NONE)
				{
					Grid temp = new Grid(this);
					temp.nextTurn(i, j);
					Node node = findInTree(temp, HEAD);
					Node temp2;
					if (node != null)
					{
						System.out.println("#####");
						temp2  = node; 
						vec[offset++] = temp2;
						continue;
					}
					else 	
						temp2 = new Node(temp);
					
					if (temp.isFinished() == Val.NONE)
							temp2.genBranches();
					vec[offset++] = temp2;
				}
			}
		}
		
		return vec;
	}
	
	public static Node findInTree(Grid g, Node curr){
		if (areIden(g, curr.g))
			return curr;
		if (curr.branches == null)
			return null;
		
		for (int i = 0; i < curr.branches.length; i++){
			Node temp = findInTree(g, curr.branches[i]);
			if (temp != null)
				return temp;
		}
		return null;
	}
	
	public static boolean areIden(Grid g1, Grid g2){
		for (int i = 0; i < g1.grid.length; i++)
			for (int j = 0; j < g1.grid.length; j++)
				if (g1.grid[i][j] != g2.grid[i][j])
					return false;
		return true;
	}
	
	/* ---------------------------------------- ****** --------------------------------------------*/
	static Node HEAD; 
	// X is max, Y is min
	
	public static void fixValues(Node cur){
		int max = -1;
		int min = 1;
		
		if (cur.g.isFinished() != Val.NONE)
		{
			cur.utility = cur.g.isFinished().num;
//			System.out.println(cur + "\n utility is " + cur.utility);
			return;
		}	
		
		for (int i = 0; i < cur.branches.length; i++){
			fixValues(cur.branches[i]);
		}
		
		for (int i = 0; i < cur.branches.length; i++){
			if (cur.branches[i].utility < min)
				min  = cur.branches[i].utility;
			
			if (cur.branches[i].utility > max)
				max = cur.branches[i].utility;
		}
		
		if (cur.g.turn == Val.X)
			cur.utility = max;
		else
			cur.utility = min;
	}
	
	public static int getDiff (Grid first, Grid after){
		for (int i = 0; i < first.grid.length; i++)
			for (int j = 0; j < first.grid[0].length; j++){
				if (first.grid[i][j] != after.grid[i][j])
					return Grid.convert(i, j)+1;
			}
		return -1;
	}
	
	public Vector<Integer> getBestMove(){
		Node current = findInTree(this, HEAD);
		int max = -2;
		int min = 2;
		for (Node n : current.branches){
			if (n.utility > max)
				max  = n.utility;
			
			if (n.utility < min)
				min  = n.utility;
		}
		
		Vector<Integer> vec = new Vector<Integer>();
		if (this.turn == Val.X){
			for (Node n : current.branches){
				if (n.utility == max){
					vec.add(getDiff(this, n.g));
				}
			}
		}
		else {
			for (Node n : current.branches){
				if (n.utility == min){
					vec.add(getDiff(this, n.g));
				}
			}		
		}
		return vec;
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException
	{
    int width = 3;
    if (args.length != 0)
      width = Integer.parseInt(args[0]);

		Scanner sc = new Scanner(System.in);
//		System.out.println("what is your desired width?? usually it's 3, but I challenge you to win in any other thing ;P");
//		width = sc.nextInt();
		
		HEAD = new Node(new Grid(width));
		System.out.println("Let me understand this game for a minute... Tic Tac Toe, Right??  I am a beginner you know");
		Spin r = new Spin();
		r.start();
		HEAD.genBranches();
		fixValues(HEAD);
		r.stop();
		System.out.println("\b\b" + "  ");
		System.out.flush();

		System.out.println(counter);
		Grid g;
		while(true){
			g = new Grid(width);
			System.out.println("WANT X OR O? maybe u should give up :P (g)");
			String temp = sc.next().toLowerCase();
			if(temp.equals("x"))
			{
				computer = Val.O;
				human = Val.X;
			}
			else if (temp.equals("o"))
			{
				computer = Val.X;
				human = Val.O;
			}
			else if (temp.equals("g")){
				System.out.println("Have a nice day");
				break;
			}
			else
				continue;
			
			
			System.out.println(g);
			while (g.isFinished() == Val.NONE)
			{
				System.out.println("turn is " + g.turn);
				int index;
				if (g.turn != computer){
					index = sc.nextInt();
					if (index == -1)
						break;
				}
				else
				{
					Vector<Integer> vec= g.getBestMove();
					index = vec.get((int) (Math.random() * vec.size()));
					System.out.println("computer thinking !!");
					spinning();
					System.out.println("computer chose " + index);
					Thread.sleep(800);
				}
			
				g.nextTurn(index);
				System.out.println(g);
			}
			System.out.println("CONGRATULATION " + g.isFinished());

			spinning();
			System.out.println();
			Thread.sleep(1500);
			System.out.println();
			Thread.sleep(1500);
		}
	}
	static int  counter = 0;
}

class Node implements Serializable{
	
	private static final long	serialVersionUID	= 1L;
	
	Grid g;
	int utility;
	Node[] branches;
	
	public Node(Grid g){
		this.g = new Grid(g);
		Grid.counter++;
//		if (Grid.counter % 10000 == 0)
//			System.out.println(Grid.counter);
	}
	
	public void genBranches(){
		if (branches == null)
			branches = g.genAllNodes(); 
		else 
			System.out.println("##");
	}
	
	public String toString(){
		return g.toString();
	}
}

class Spin extends Thread{
	public void run()
	{
		char[] cs = {'\\', '|', '/', '-'};
		int counter = 0;
		while (true){
			System.out.flush();
			System.out.print("\b\b" + cs[counter % cs.length]  + " ");
			try
			{
				sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			counter = ++counter % cs.length;
		}
		
	}
}
