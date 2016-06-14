# hive-blocks
The project supports running conditional blocks with hive queries

You can provide it a xml in below construct:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<blocks id="hive-blocks" base-path="C:/Users/manojkumar.vohra/Desktop/hblocks/hive-block-new/hive-blocks/samples/">
  <block id="first-block">
    
    <!-- GLOBAL BLOCK LEVEL VARIABLES-->
    <variable name="b1Var1" type="numeric"/>
    <variable name="b1Var2" type="string"/>
    <variable name="addVal" type="numeric"/>
    <variable name="from_date" type="timestamp"/>
    <variable name="to_date" type="timestamp"/>
    
    <!-- SELECT QUERY RESULT VALUES INTO VARIABLES-->
    <export id="export_both_vars" query-file="export_vars.hql"/>
    <export id="export_dates" query-file="export_dates.hql"/>

    <print text="Exported values to date variables -- from_date is :from_date | to_date is :to_date"/>
    
    <!-- IF BLOCK-->
    <if id="if_gt_2" condition=":b1Var1 > 2 AND :b1Var2 == 'abc'">
    
        <!-- IF BLOCK LEVEL VARIABLES-->
        <variable name="b1Var2" type="string" value="overriden-b1Var2"/>
    
        <!-- QUERY TO BE EXECUTED, VARIABLES SUBSTITUTED IF ANY FOUND IN QUERY-->
        <query id="pre_sub_if" query-file="pre_if.hql" />

        <!-- NESTED IF ELSE BLOCK-->
        <if id="sub_if" condition=":b1Var2 == 'overriden-b1Var2'">
      	     <query id="insert_dummy" query-file="insert_into_dummy.hql" />
        </if>
        <else id="sub_else">
            <print text="sub_if not executed"/>
        </else>
        <query id="post_if" query-file="post_if.hql" />
        
        <!-- FOR EACH RECORD FETCHED FROM QUERY, ITERATE AND EXECUTE SUB ELEMENTS-->
        <for id="for each dummy value" query-file="iterate_dummy.hql">
          <variable name="dummyfield" type="numeric"/>
          <print text="Got Dummy Value=:dummyfield"/>
        </for> 
    </if>

    <export id="add vars and prepare new" query-file="add_to_vars.hql"/>
    <query id="last insert" query-file="insert_into_dummy_plus.hql" />

    <!-- IF ELSEIF .... ELSE BLOCK: TESTING ELSEIF EXECUTION-->
    <if id="nano_if" condition=":addVal != 4">
          <print text="added value is not 4"/>
    </if>
    <elseif id="nano_else_if" condition=":addVal == 4">
          <print text="added value is 4"/>
    </elseif>
    <else id="else">
            <print text="okay its something else then"/>
    </else>

    <!-- IF ELSEIF .... ELSE BLOCK: TESTING ELSE EXECUTION-->
    <if id="nano_if" condition=":addVal == 5">
          <print text="added value is not 5"/>
    </if>
    <elseif id="nano_else_if" condition=":addVal == 6">
          <print text="added value is 6"/>
    </elseif>
    <else id="else">
            <print text="okay its something else then"/>
    </else>

  </block>
</blocks>
```

Note: The supporting files for above block configuration can be found in sample folder in project.
(Execution log can be seen in sample folder)

- Here the execution can be subdivided into blocks where each block signify a pl/sql block with multiple IFs, Loops, Queries or nesting of that with in each other.
- The execution order of blocks is determined by order they appear in input xml config.
- The execution order of elements (at same level) within block is determined by order they appear.
- With in elements the execution order of nested elements (at same level) is determined by order they appear.
- A \<block\> signify a piece of confined executible. It can contain:
  -  \<variable\>
  -  \<export\> (export would assign values to variable defined in BLOCK)
  -  \<if\>
  -  \<elseif\>
  -  \<else\>
  -  \<for\>
  -  \<query\>
  -  \<print\>
- A \<variable\> signify a variable which can be assigned a value using \<export\> elements.
  -   \<variable\> defined at block level remain valid at block level
  -   Block level variables can be overridden by defining variable by same name in \<if\> or \<for\>
  -   You can have variable with same names between different blocks.
  -   Variable can referenced within query or If conditions by prepending ':' in front of variable name.
  -   Variable substitution works by first getting values within immediate element (\<if\> or \<for\>), If no value found the search for variable value proceeds to element's parent until it reaches the block level variable look up.
- A \<export\> signify a query whose result set is assigned to variables.
- A \<if\> block signify a conditional block whose execution depends on its condition. It can contain:
  -   \<variable\>
  -   \<export\> (export would assign values to variable defined in IF)
  -   \<if\>
  -   \<elseif\>
  -   \<else\>
  -   \<query\>
  -   \<for\>
  -   \<print\>
- A \<elseif\> block signify a conditional block whose execution depends on its condition provided its preceeding \<if\> or \<elseif>\ did not execute. It can contain: 
  -   \<variable\>
  -   \<export\> (export would assign values to variable defined in ELSEIF)
  -   \<if\>
  -   \<elseif\>
  -   \<else\>
  -   \<query\>
  -   \<for\>
  -   \<print\>
- A \<else\> block signify a last block in conditional if-elseif construct, which executes only if its preceeding \<if\> or \<elseif>\ did not execute. It can contain: 
  -   \<variable\>
  -   \<export\> (export would assign values to variable defined in ELSE)
  -   \<if\>
  -   \<elseif\>
  -   \<else\>
  -   \<query\>
  -   \<for\>
  -   \<print\>
- A \<for\> block signify a for loop whose execution depends on the rows returned by executing query in queryfile. It can contain:
  -   \<variable\>
  -   \<export\> (export would assign values to variable defined in FOR)
  -   \<if\>
  -   \<elseif\>
  -   \<else\>
  -   \<query\>
  -   \<for\>
  -   \<print\>
- A \<print\> signify a text/comment which can be printed with in execution for information purpose. Text can contain variables in format :variableName

Requirements 
------------
JDK 1.7 & above
MVN 3

How to execute samples?
-----------------------
- Download project, run as maven build targets: clean, install
- Take the Hive-Blocks-0.0.1-jar-with-dependencies.jar from target directory and hive_blocks.properties from samples folder.
- Change db properties in hive_blocks.properties
- With in samples folder, alter file hive_blocks.xml - set base-path = directory where hql files can be located
- Execute class com.blocks.main.HBlockExecutor and provide it two arguments in order: blocks-xml-config, blocks properties file path
  - java -cp Hive-Blocks-0.0.1-jar-with-dependencies.jar com.blocks.main.HBlockExecutor C:/Users/manojkumar.vohra/Desktop/hive-blocks/hive-blocks/samples/hive_blocks.xml C:/Users/manojkumar.vohra/Desktop/hive-blocks/hive-blocks/hive_blocks.properties
