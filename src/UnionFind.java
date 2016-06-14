public class UnionFind {

	private int[] parent;
	private int[] rank;

	public UnionFind(int n) {
		parent = new int[n];
		rank = new int[n];
		for (int i = 0; i < n; i++) {
			parent[i] = i;
			rank[i] = 0;
		}
	}

	public int find(int item) {
		try {
			if (item >= parent.length) {
				System.out.print("");
			}

			if (parent[item] == item) {
				return item;
			} else {
				parent[item] = find(parent[item]);
				return parent[item];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void union(int item1, int item2) {
		int rootItem1 = find(item1);
		int rootItem2 = find(item2);
		if (rootItem1 == rootItem2)
			return;

		// make root of smaller rank point to root of higher rank
		if (rank[rootItem1] < rank[rootItem2]) {
			parent[rootItem1] = rootItem2;
		} else {
			parent[rootItem2] = rootItem1;
		}

	}

}