package com.example.droptweet.twitter;
import twitter4j.auth.*;

public class RequestTokenPair
{
	public RequestToken token;
	public String verifier;
	
	public RequestTokenPair(RequestToken token, String verifier)
	{
		this.token = token;
		this.verifier = verifier;
	}
}
