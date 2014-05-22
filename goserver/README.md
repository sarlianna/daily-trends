Server setup:
--------------

TODO:

- move some of the post/put body parsing into middleware if possible
- break up some of the more repetitive parsings into methods

For now just using Goji and sqlite3, and writing raw json strings to response writers.

Models:

- Tag: Just some text that represents an action or event.
- Occurance: The bulk of the app, links a tag to a day and some additional
    info like the degree it happened and whether it was good or bad.
    These should be unique for a tag/date combination.

Routes:

Basically they're just CRUD for both tags and days right now.  Days actually link to
Occurances, and have all the same data.  Maybe naming the routes something different is
just confusing.
