# documentation-manager
Proof of concept for dynamically generating documentation based on repo scanning.

Goal is to be able to provide this code a Github organization, let it scan through all repositories and then end up with data on how the repos work:
- Which repos exist
- Do they expose any endpoints?
- Do they call each other via REST calls? If so, which ones, and who connects to what?
- Do they use queues? If so, how do they connect to this? (what exchange, routing key, ...)
- Do they use AWS features like SNS, SQS, etc? If so, what do they connect to?
- Do they connect to databases? If so, which ones? Which tables or stored procs are called? Read-only or writing as well?
- Are there any scheduled jobs part of this app?
- What version of core frameworks do they use?

Using Spoon to generate logical models of each class, which is then scanned through with a series of Matchers (for example, a RabbitMQ matcher). If the matcher finds something, it tries to resolve values behind it. In the example of a RabbitMQ matcher finding a match, it will try to resolve the exchange and queue used.

The goal is not to be exhaustive - but specifically focus on standard Spring Microservices. Matchers should be relatively easy to add.
