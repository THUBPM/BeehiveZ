package cn.edu.thss.iise.beehivez.server.index.bplustree;

/// <summary>
/// No such key found for attempted retrieval.
/// </summary>
public class BplusTreeKeyMissing extends Exception {
	public BplusTreeKeyMissing(String message)// : base(message)
	{
		// do nothing extra
		super(message);
	}
}