<h3>Goal</h3>
<p>Main purpose of this tutorial is to provide full backend application, which should use hyperon engine and peristence mechanism for storing recalculated data.
</p>
<span>Prerequisites:</span>
<ul>
<li>Java 11</li>
<li>Gradle 4.10.x</li>
<li>Spring Boot 2.1+</li>
<li>Hyperon Studio with H2 database (default for bundle distribution)</li>
<li>Another H2 instance for business logic entities (available on github)</li>
</ul>
<span>This tutorial requires from developer:</span>
<ul>
<li>general knowledge of Hyperon Studio</li>
<li>how to create context in Studio</li>
<li>basic knowledge of Hyperon aspects, like how to work with domain, parameters and functions using engine</li>
</ul>
<span>Steps to complete this tutorial:</span>
<ol>
<li>Download Hyperon Studio <a href="http://hyperon.io/download">(default for bundle distribution)</a> with prepared data, required for this tutorial</li>
<li>Generate context classes using <i>code generation tool</i>, based on <i>bundle.def</i></li>
<li>Configure application to use hyperon engine and persistence mechanism</li>
<li>Create REST endpoints in backend using Spring</li>
<li>Build bundle root with quote on startup, based on Domain from Hyperon Studio - use hyperon engine and then persist it</li>
<li>Read bundle root from application database, if it was already created. If not, create as defined in step 5</li>
<li>Provide recalculation of full quote, whenever REST - /quote GET method was called and then save bundle root using hyperon persistence</li>
</ol>
<p>
<h3>Github project </h3>
Full code example is available on <a href="https://github.com/hyperon-io/motor-insurance-advanced">github</a>