# java-crdt

Collection of common CRDTs for Java.

### CRDT Sets

- G-Set: Grow-Only Set that allows only addition operations.
- 2P-Set: 2-Phase Set which allows removing element only once.
- LWW-Set: Last-Write-Wins-Element Set. Uses 'timestamps' associated with addition and deletion operations for picking the winner.
- OR-Set: Observed-Removed Set. Associates unique tag with each addition operation. Deletion is applied for particular tag.
- OUR-Set: Observed-Updated-Removed Set. Uses unique identifiers (UUIDs) for distingishing different elements within a set. Conflict resolution is based on the 'lastModified' timestamp value associated with each element state in the set.
