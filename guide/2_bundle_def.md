<h1>Contract between developer and Hyperon Studio - bundle.def</h1>
<b>bundle.def</b> file holds information about context definition from Hyperon Studio. More about this, can be read
<a href="http://hyperon.io/tutorials/hyperon-concepts-context">here</a>.
This file can be created in two ways:
<ol>
<li>Export from Hyperon Studio, where context is defined,</li>
<li>Create by yourself and then import it to Hyperon Studio.</li>
</ol>
What's important is that bundle.def can be used with or without Hyperon Peristence. To make it work with
Hyperon Persistence, in studio/file, entities must be defined as <i>table</i> elements, with optional properties as
<i>columns</i>. If property of a table is defined as <i>column</i>, then value of this property will be stored in table's column. If property, is defined without <i>column</i> descriptor, then value of it, will be stored within Hyperon Studio database structure as JSON. 
<p>
<b>
It is developer's responsibility to create proper structure, that matches bundle.def.</b> For this project, there is script available at github repository <a href="https://github.com/hyperon-io/motor-insurance-advanced/blob/master/src/main/resources/sql/schema.sql">here</a>, that matches context. Whenever there should be changes done to context's structure, developer must <b>always remember</b> to keep it synchronized with application.
</p>
<span>This is short example of <i>bundle.def</i> file for this project:</span>
<code>

    def /ROOT
        quote Quote   @desc quote (with all available options)

    def Quote table QUOTE                            @desc Insurance Policy / Proposal
      driver Driver                                      @desc insured driver
      options Option*                                    @desc rating variations
      planCode string column PLAN_CODE                   @desc code of rating plan for this policy
      vehicle Vehicle                                    @desc insured vehicle

    def Vehicle table VEHICLE                        @desc Vehicle data
      make string                                        @desc Manufacturer of the vehicle
      makeId integer                                     @desc Manufacturer ID
      model string                                       @desc Vehicle model
      modelId integer column MODEL_ID                    @desc Vehicle model ID
      primaryUse string                                  @desc Primary use of vehicle [commute (to work or school), pleasure, business]
      productionYear integer                             @desc Vehicle production year (in form YYYY)
      typeId integer column TYPE_ID                      @desc Vehicle type ID

    ...
</code>
<span>Full example for this project is available on
<a href="https://github.com/hyperon-io/motor-insurance-advanced/blob/master/src/main/resources/bundle.def">github</a></span>
<h3>Description of important elements in this file</h3>
<ol>
    <li>def /ROOT - root point of which all entities data is managed by Hyperon Peristence. It might have mapping to table, like Quote as well. If not provided then it can also be specified using <code>HyperonPersistenceFactory::setBundleTable</code> with root table name</li>
    <li>def Vehicle table VEHICLE - VEHICLE table is business table and all it's entities data is managed by Hyperon Peristence</li>
    <li>def Quote table QUOTE - QUOTE table is business table and all it's entities data is managed by Hyperon Peristence</li>
    <li>options Option* - * means that, it has collection of elements of type OPTION</li>
    <li>planCode string column PLAN_CODE - table QUOTE has column PLAN_CODE and will be mapped to planCode element as String</li>
    <li>make string - is managed by Hyperon Peristence and it is not stored in business VEHICLE table, but within ROOT table definition as JSON format</li>
</ol>
<h3>Requirements for proper table definition managed by persistence engine</h3>
<p>Root table is specific, since it must store not mapped values as JSON. In this tutorial, bundle root is named <i>PERSISTENCE_BUNDLE</i> and has structure:
<code>

	CREATE TABLE PUBLIC.PERSISTENCE_BUNDLE
	(
	    ID NUMBER, 
	    REVISION long NOT NULL, 
	    CREATED TIMESTAMP (6) NOT NULL,
	    UPDATED TIMESTAMP (6), 
	    VALUE CLOB  // holds not mapped values as JSON format
	);
</code>
</p>
<p>Other tables, that are outside of Hyperon, which should be managed by Hyperon Persistence engine, must have four mandatory columns:</p>
<ul>
<li>ID - primary key </li>
<li>BID- bundle id</li> 
<li>PARENTID - id of parent entity</li>
<li>COLLNAME - column name, which is mapping of a parent's column</li> 
</ul>
<span>Example for Quote table</span>
<code>

    create table PUBLIC.QUOTE
    (
      ID LONG NOT NULL,
      BID LONG NOT NULL, 
      PARENTID LONG NOT NULL, 
      COLLNAME VARCHAR2(100) NOT NULL, 
      PLAN_CODE VARCHAR2(100) NOT NULL, // extra business column
      PRIMARY KEY ("ID")
    );
</code>
Of course root table name and all mandatory column names can be different. It depends on project requirements. Everything can be setup with <b>HyperonPersistenceFactory</b>.
<h4>Development hint</h4>
It is good practice to use json structure for persisting data, while development process is on going, and application/domain is not yet stable.