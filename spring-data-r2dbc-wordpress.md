<!-- wp:paragraph -->
<p>Not too long ago, a reactive variant of the JDBC driver was released. Known as R2DBC. It allows data to be streamed asynchronously to any endpoints that have subscribed to it. Using a reactive driver like R2DBC together with Spring WebFlux allows you to write a full application that handles receiving and sending of data asynchronously. In this post, we will focus on the database. From connecting to the database and then finally saving and retrieving data. To do this, we will be using Spring Data. As with all Spring Data modules, it provides us with out of the box configuration. Decreasing the amount of boilerplate code that we need to write to get our application setup. On top of that, it provides a layer upon the database driver that makes doing the simple tasks easier and the more difficult tasks a little less painful.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>For the content of this post, I am making use of a Postgres database. At the time of writing only Postgres, H2 and Microsoft SQL Server have their own implementations of R2DBC drivers.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>I have previously written two posts about reactive Spring Data libraries, one on <a href="https://lankydanblog.com/2017/07/16/a-quick-look-into-reactive-streams-with-spring-data-and-mongodb/" target="_blank" rel="noreferrer noopener" aria-label=" (opens in a new tab)">Mongo</a> and another about <a rel="noreferrer noopener" aria-label="Cassandra (opens in a new tab)" href="https://lankydanblog.com/2017/12/11/reactive-streams-with-spring-data-cassandra/" target="_blank">Cassandra</a>. You might have noticed that neither of these databases are RDBMS databases. Now there are other reactive drivers available for a long time (I wrote the Mongo post nearly 2 years ago) but at the time of writing a reactive driver for a RDBMS database is still a pretty new thing. This post will follow a similar format to those.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>Furthermore, I have also written a post about using <a href="https://lankydanblog.com/2018/03/15/doing-stuff-with-spring-webflux/" target="_blank" rel="noreferrer noopener" aria-label="Spring WebFlux (opens in a new tab)">Spring WebFlux</a> which I mentioned in the introduction. Feel free to have a look at that if you are interested in producing a fully reactive web application.</p>
<!-- /wp:paragraph -->

<!-- wp:heading {"level":3} -->
<h3>Dependencies</h3>
<!-- /wp:heading -->

<!-- wp:paragraph -->
<p>[gist https://gist.github.com/lankydan/38b799a72c630d843c73ca5f6e75ac51 /]</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>There are a few things to point out here.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>The more you use Spring Boot, the more you will get used to importing a single <code>spring-boot-starter</code> dependency for the cool thing that you want to do. For example, I hoped that there would have been a <code>spring-boot-starter-r2dbc</code> dependency, but unfortunately, there is not one. Yet. Simply put, this library is on the newer side and at the time of writing, does not have its own Spring Boot module that contains any dependencies it needs along with faster setup via auto-configuration. I am sure these things will come at some point and make setting up a R2DBC driver even easier.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>For now, we will need to fill in a few extra dependencies manually.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>Furthermore, the R2DBC libraries only have Milestone releases (more proof of them being new) so we need to make sure we bring in the Spring Milestone repository. I will probably need to update this post in the future when it gets a release version.</p>
<!-- /wp:paragraph -->

<!-- wp:heading {"level":3} -->
<h3>Connecting to the database</h3>
<!-- /wp:heading -->

<!-- wp:paragraph -->
<p>Thanks to Spring Data doing a lot of the work for us, the only Bean that needs to be created manually is the <code>ConnectionFactory</code> that contains the database's connection details:</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>[gist https://gist.github.com/lankydan/93431efc72485a1468ba489c08e66b40 /]</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>The first thing to notice here is the extension of <code>AbstractR2dbcConfiguration</code>. This class contains a load of Beans that we no longer need to manually create. Implementing <code>connectionFactory</code> is the only requirement of the class as it is required to create the <code>DatabaseClient</code> Bean. This sort of structure is typical of Spring Data modules so it feels quite familiar when trying out a different one. Furthermore, I'd expect this manual configuration to be removed once auto-configuration is available and be solely driven via the <code>application.properties</code>.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>The four properties defined by the <code>PostgresqlConnectionFactory</code> are the bare minimum to get it working. Any less and you will experience exceptions during startup.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>Using this configuration, Spring is able to connect to a running Postgres instance.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>The final piece of noteworthy information from this example is the use of <code>@EnableR2dbcRepositories</code>. This annotation instructs Spring to find any repository interfaces that extend Spring's <code>Repository</code> interface. This is used as the base interface for instrumenting Spring Data repositories. We will look at this a little closer in the next section. The main piece of information to take away from here is that you need to use the <code>@EnableR2dbcRepositories</code> annotation to fully leverage Spring Data's capabilities.</p>
<!-- /wp:paragraph -->

<!-- wp:heading {"level":3} -->
<h3>Creating a Spring Data Repository</h3>
<!-- /wp:heading -->

<!-- wp:paragraph -->
<p>As touched on above, in this section we will look at adding a Spring Data Repository. These repositories are a nice feature of Spring Data, meaning that you don't need to write out a load of extra code to simply write a query. Unfortunately, at least for now, Spring R2DBC cannot infer queries in the same way that other Spring Data modules currently do (I am sure this will be added at some point). This means that you will need to use the <code>@Query</code> annotation and write the SQL by hand. Let's take a look:</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>[gist https://gist.github.com/lankydan/ec23a1ac53740365668bec2a8763fa79 /]</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>This interface extends <code>R2dbcRepository</code>. This in turn extends <code>ReactiveCrudRepository</code> and then down to <code>Repository</code>. <code>ReactiveCrudRepository</code> provides the standard CRUD functions and from what I understand, <code>R2dbcRepository</code> does not provide any extra functions and is instead an interface created for better situational naming.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p><code>R2dbcRepository</code> takes in two generic parameters, one being the entity class that it takes as input and produces as output. The second being the type of the Primary Key. Therefore in this situation, the <code>Person</code> class is being managed by the <code>PersonRepository</code> (makes sense) and the Primary Key field inside <code>Person</code> is an <code>Int</code>.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>The return types of functions in this class and the ones provided by <code>ReactiveCrudRepository</code> are <code>Flux</code> and <code>Mono</code> (not seen here). These are Project Reactor types that Spring makes use of as the default Reactive Stream types. <code>Flux</code> represents a stream of multiple elements whereas a <code>Mono</code> is a single result.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>Finally, as I mentioned before the example, each function is annotated with <code>@Query</code>. The syntax is quite straight forward, with the SQL being a string inside the annotation. The <code>$1</code> (<code>$2</code>, <code>$3</code>, etc... for more inputs) represents the value input into the function. Once you have done this, Spring will handle the rest and pass the input(s) into their respective input parameter, gather the results and map it to the repository's designated entity class.</p>
<!-- /wp:paragraph -->

<!-- wp:heading {"level":3} -->
<h3>A very quick look at the entity</h3>
<!-- /wp:heading -->

<!-- wp:paragraph -->
<p>Not going to say much here but simply show the <code>Person</code> class used by the <code>PersonRepository</code>.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>[gist https://gist.github.com/lankydan/74e8736f9f6618172042686ea456a7de /]</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>Actually, there is one point to make here. <code>id</code> has been made nullable and provided a default value of <code>null</code> to allow Postgres to generate the next suitable value itself. If this is not nullable and an <code>id</code> value is provided, Spring will actually try to run an update instead of an insert upon saving. There are other ways around this, but I think this is good enough.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>This entity will map to the <code>people</code> table defined below:

[gist https://gist.github.com/lankydan/842f1521134ab4c2eb214fdafc624b9c /]</p>
<!-- /wp:paragraph -->

<!-- wp:heading {"level":3} -->
<h3>Seeing it all in action</h3>
<!-- /wp:heading -->

<!-- wp:paragraph -->
<p>Now let's have a look at it actually doing something. Below is some code that inserts a few records and retrieves them in a few different ways:</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>[gist https://gist.github.com/lankydan/b6a9d7550b4ba6efe5c3846135062c43 /]</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>One thing I will mention about this code. There is a very real possibility that it executes without actually inserting or reading some of the records. But, when you think about it. It makes sense. Reactive applications are meant to do things asynchronously and therefore this application has started processing the function calls in different threads. Without blocking the main thread, these asynchronous processes might never fully execute. For this reason, there are some <code>Thread.sleep</code> calls in this code, but I removed them from the example to keep everything tidy.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>The output for running the code above would look something like the below:</p>
<!-- /wp:paragraph -->

<!-- wp:preformatted -->
<pre class="wp-block-preformatted">2019-02-11 09:04:52.294  INFO 13226 --- [           main] reactor.Flux.ConcatMap.1                 : onSubscribe(FluxConcatMap.ConcatMapImmediate)<br>2019-02-11 09:04:52.295  INFO 13226 --- [           main] reactor.Flux.ConcatMap.1                 : request(unbounded)<br>2019-02-11 09:04:52.572  INFO 13226 --- [actor-tcp-nio-1] reactor.Flux.ConcatMap.1                 : onNext(Person(id=35, name=Dan Newton, age=25))<br>2019-02-11 09:04:52.591  INFO 13226 --- [actor-tcp-nio-1] reactor.Flux.ConcatMap.1                 : onNext(Person(id=36, name=Laura So, age=23))<br>2019-02-11 09:04:52.591  INFO 13226 --- [actor-tcp-nio-1] reactor.Flux.ConcatMap.1                 : onComplete()<br>2019-02-11 09:04:54.472  INFO 13226 --- [actor-tcp-nio-2] com.lankydanblog.tutorial.Application    : findAll - Person(id=35, name=Dan Newton, age=25)<br>2019-02-11 09:04:54.473  INFO 13226 --- [actor-tcp-nio-2] com.lankydanblog.tutorial.Application    : findAll - Person(id=36, name=Laura So, age=23)<br>2019-02-11 09:04:54.512  INFO 13226 --- [actor-tcp-nio-4] com.lankydanblog.tutorial.Application    : findAllByName - Person(id=36, name=Laura So, age=23)<br>2019-02-11 09:04:54.524  INFO 13226 --- [actor-tcp-nio-5] com.lankydanblog.tutorial.Application    : findAllByAge - Person(id=35, name=Dan Newton, age=25)</pre>
<!-- /wp:preformatted -->

<!-- wp:paragraph -->
<p>A few things to take away here:</p>
<!-- /wp:paragraph -->

<!-- wp:list -->
<ul><li><code>onSubscribe</code> and <code>request</code> occur on the main thread where the <code>Flux</code> was called from. Only <code>saveAll</code> outputs this since it has included the <code>log</code> function. Adding this to the other calls would have lead to the same result of logging to the main thread. </li><li>The execution contained within the subscribe function and the internal steps of the <code>Flux</code> are ran on separate threads.</li></ul>
<!-- /wp:list -->

<!-- wp:paragraph -->
<p>This is not anywhere close to a real representation of how you would use Reactive Streams in an actual application but hopefully demonstrates how to use them and gives a bit of insight into how they execute.</p>
<!-- /wp:paragraph -->

<!-- wp:heading {"level":3} -->
<h3>Conclusion</h3>
<!-- /wp:heading -->

<!-- wp:paragraph -->
<p>In conclusion, Reactive Streams have come to some RDBMS databases thanks to the R2DBC driver and Spring Data that builds a layer on top to make everything a bit tidier. By using Spring Data R2DBC we are able to create a connection to a database and start querying it without the need of to much code. Although Spring is already doing a lot for us, it could be doing more. Currently, it does not have Spring Boot auto-configuration support. Which is a bit annoying. But, I am sure that someone will get around to doing it soon and make everything even better than it already is.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>The code used in this post can be found on my <a rel="noreferrer noopener" aria-label="GitHub (opens in a new tab)" href="https://github.com/lankydan/spring-data-r2dbc" target="_blank">GitHub</a>.</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>If you found this post helpful, you can follow me on Twitter at <a href="https://twitter.com/LankyDanDev" target="_blank" rel="noreferrer noopener" aria-label="@LankyDanDev (opens in a new tab)">@LankyDanDev</a> to keep up with my new posts.</p>
<!-- /wp:paragraph -->