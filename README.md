# Player Reviewer
<img src="https://media.forgecdn.net/avatars/195/299/636878207268497978.png" width=100px height=100px>

## Features
This plugin allows users to apply for ranks, which are then rated by users with permissions to do so. The ratings are scored of 0-100 in criteria set within the config (custom criteria is limited to local file config at the moment, this will be updated to support SQL databases soon). An example of possible custom criteria is:

- Quality
- Size
- Creativity

The default config options for local file configs are:

- Atmosphere
- Originality
- Terrain
- Structure
- Layout

The application logs the user's location and any user with permissions can teleport to the location to view a build relating to the application (for example). Another option is to log the location of the plot the user is currently in (Requires PlotSquared dependency).

When a staff member (with permissions to approve) has reviewed the application and the ratings given they can approve or deny, ultimately resulting in either the rank being granted or not.

The applications and ratings are logged either locally within the applications.yml file in the plugin folder or to a database, which can be configured within the applications.yml file too.

As it stands, currently the only permissions plugin supported are PermissionsEx and LuckPerms. Similarly, the only plot plugin supported at the moment is PlotSquared.

---

## Commands

* **/pr apply \<rank\>** - *Apply For The Specified Rank*
* **/pr validranks** - *Displays All Rank Names On The Server* **\[ALIASES: vr, ranks\]**
* **/pr rate \<player\> \<criteria1\> \<criteria2\> …** - *Rate An Open Application With A Rating of 0-100 In Each Criteria*
* **/pr gotoplot** - *Teleport To Location Or Plot Of Open Application* **\[ALIASES: plot\]**
* **/pr ratings** - *View Ratings Of Open Application (Requires Perm pr.ratings To View Other Users Applications)*
* **/pr approval \<approve/deny\>** - *Approve Or Deny Open Applications*
* **/pr removeapplication \<name\>** - *Remove An Open Application By Specifying Relevant Player Name* **\[ALIASES: ra, remap\]**
* **/pr version** - *Displays Plugin Version Information*
* **/pr help** - *Displays Plugin Help Information*
* **/pr apphelp** - *Displays application submission help*
* **/pr status** - *Displays player application status*
* **/pr pos1** - *Register chunky position 1*
* **/pr pos1** - *Register chunky position 2*
* **/pr cam** - *Register a chunky camera*
* **/pr chunkysettings** - *Returns position, chunk and parameter details* **[ALIASES: cs]**
* **/pr setparam** - *Sets a chunky JSON parameter* **[ALIASES: sp]**
* **/pr removeparam** - *Removes a chunky JSON parameter* **[ALIASES: rp]**
* **/pr viewparams \<raw/list\>** - *View currently set chunky JSON parameters* **[ALIASES: vp]**
* **/pr clearparams \[\<confirm/deny\>\]** - *Clear all currently set chunky JSON parameters (requires confirmation)* **[ALIASES: rp]**


---

## Permissions

* **pr.use** - *Allows the use of the pr command*
* **pr.apply** - *Allows the use of the /pr apply command*
* **pr.validranks** - *Allows the use of the /pr validranks command*
* **pr.rate** - *Allows the use of the /pr rate command*
* **pr.gotoplot** - *Allows the use of the /pr gotoplot command*
* **pr.ratings** - *Allows the use of the /pr ratings command for other users applications*
* **pr.approval** - *Allows the use of the /pr approval command*
* **pr.removeapplication** - *Allows the use of the /pr removeapplication comman*
* **pr.version** - *Allows the use of /pr version*
* **pr.help** - *allows the use of /pr help*
* **pr.apphelp** - *allows the use of /pr apphelp*
* **pr.status** - *allows the use of /pr status*
* **pr.pos1** - *allows the use of /pr pos1*
* **pr.pos2** - *allows the use of /pr pos2*
* **pr.cam** - *allows the use of /pr cam*
* **pr.chunkysettings** - *allows the use of /pr chunkysettings*
* **pr.setparam** - *allows the use of /pr setparam*
* **pr.removeparam** - *allows the use of /pr removeparam*
* **pr.viewparams** - *allows the use of /pr viewparams*
* **pr.clearparams** - *allows the use of /pr clearparams*


---

## Configuration and Installation

Drag and drop the most recent version of the plugin into your plugins folder. Restart or start the server, the config file will generate.

The config file contains:

> User-Details:\
> &nbsp;&nbsp;UseSQL: true\
> &nbsp;&nbsp;HOSTNAME: hostname\
> &nbsp;&nbsp;DATABASE: database name\
> &nbsp;&nbsp;PORT: port number\
> &nbsp;&nbsp;USER: username\
> &nbsp;&nbsp;PASS: password\
> Application-Settings:\
> &nbsp;&nbsp;Use-Plot-Locations: true\
> &nbsp;&nbsp;Criteria:\
> &nbsp;&nbsp;\- atmosphere\
> &nbsp;&nbsp;\- originality\
> &nbsp;&nbsp;\- terrain\
> &nbsp;&nbsp;\- structure\
> &nbsp;&nbsp;\- layout\
> &nbsp;&nbsp;Use-Config-Ranks: true\
> &nbsp;&nbsp;Application-Ranks: guest[GUEST:guest],squire,knight,baron,builder,head_builder,senior_builder
> Chunky-Config:\
> &nbsp;&nbsp;Use-Chunky: false\
> &nbsp;&nbsp;Max-Camera-Count: 4

Here you can specify:
- Whether to use SQL databases or local files for application storage
- Specify SQL database connection parameters
- Whether to use plot locations (PlotSquared dependency) or player locations
- Specify custom criteria for applications (Requires SQL database use)
- Specify whether to use/allow ranks in applications
- Specify the name of the SQL table applications are stored in
- Whether to use server ranks or specify specific ranks for application availability
- Specify ranks that can be applied for via an application (must be a valid rank on the server)
- Whether to export chunky render parameters and cameras set by users
- Maximum camera count for chunky renders

---

## SQL Database Configuration

If you are using an SQL database to store the applications, there are a few naming conventions that need to be followed (this will be user defined in the future):

The table name that you use must be set within the config file to match.
The columns must be as follows (replace 'criteria 1, criteria 2, etc' with the names of your criteria):
**settings | status | name | rank | criteria 1 | criteria 2| … | criteria n | plotloc | totalRatings | ratinglist**

---

## Chunky

Chunky parameters and cameras are exported in JSON format to the SQL table column "settings", from here you can process them and generate a render of the chunky specified within the JSON.

Cameras are labelled in the following fahsion: "camera_n" where 'n' denotes the camera number.

All of the chunky parameters can be set from in game with the /pr setparams command. As a standard with just two positions captured and a camera set, the JSON will be as follows (any additional parameters are added in prior to export):

> \{
> &nbsp;&nbsp;&nbsp;&nbsp;"camera_1": \{\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"position": \{\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"x": 39.26309100906894,\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"y": 65.49390748022948,\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"z": -236.876800963328\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\},\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"orientation": \{\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"roll": 0.0,\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"pitch": -0.8635957187819827,\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"yaw": 0.7880137870620696\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\},\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"projectionMode": "PINHOLE",\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"fov": 90.0,\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"dof": "Infinity",\
> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"focalOffset": 2.0\
> &nbsp;&nbsp;&nbsp;&nbsp;\},\
> &nbsp;&nbsp;&nbsp;&nbsp;"chunkList": [[0,-15],[0,-14],[1,-15],[1,-14],[2,-15],[2,-14]]\
> \}
---