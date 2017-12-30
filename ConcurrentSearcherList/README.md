# ConcurrentSearcherList

Given problem:
Implementation of three kinds of operations on linked list: search, insert and remove
-> Threads performing search can execute concurrently.
-> Insertions are mutually exclusive. One insert can proceed in parallel with any number of searches. However, there should be no data race. 
-> Only one thread can remove at a time. Remove is also mutually exclusive with searches and inserts.

