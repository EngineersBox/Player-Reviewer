# Player Reviewer

## Features
This plugin allows users to apply for ranks, which are then rated by users with permissions to do so. The ratings are scored of 0-100 in criteria set within the config (custom criteria is limited to local file config at the moment, this will be updated to support SQL databases soon). An example of possible custom criteria is:

- Quality
- Size
- Creativity
- The default config options for local file configs are:

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
/pr apply \<rank> - *Italic*Apply For The Specified Rank
/pr validranks [ALIASES: vr, ranks]- *Italic*Displays All Rank Names On The Server 
/pr rate \<player> <criteria1> <criteria2> … - *Italic*Rate An Open Application With A Rating of 0-100 In Each Criteria
/pr gotoplot [ALIASES: plot] - *Italic*Teleport To Location Or Plot Of Open Application
/pr ratings - *Italic*View Ratings Of Open Application (Requires Perm pr.ratings To View Other Users Applications)
/pr approval \<approve/deny> - *Italic*Approve Or Deny Open Applications
/pr removeapplication \<name> [ALIASES: ra, remap] - *Italic*Remove An Open Application By Specifying Relevant Player Name
/pr version - *Italic*Displays Plugin Version Information
/pr help - *Italic*Displays Plugin Help Information
---

## Permissions
pr.use - *Italic*Allows the use of the pr command
pr.apply - *Italic*Allows the use of the /pr apply command
pr.validranks - *Italic*Allows the use of the /pr validranks command
pr.rate - *Italic*Allows the use of the /pr rate command
pr.gotoplot - *Italic*Allows the use of the /pr gotoplot command
pr.ratings - *Italic*Allows the use of the /pr ratings command for other users applications
pr.approval - *Italic*Allows the use of the /pr approval command
pr.removeapplication - *Italic*Allows the use of the /pr removeapplication comman
pr.version - *Italic*Allows the use of /pr version
pr.help - *Italic*allows the use of /pr help
---

## Configuration and Installation
Drag and drop the most recent version of the plugin into your plugins folder. Restart or start the server, the config file will generate.

The config file contains:
>User-Details:
>  UseSQL: true
>  HOSTNAME: hostname
>  DATABASE: database name
>  PORT: port number
>  USER: username
>  PASS: password
>Application-Settings:
>  Use-Plot-Locations: true
>  Criteria:
>  - atmosphere
>  - originality
>  - terrain
>  - structure
>  - layout
>  Use-Config-Ranks: true
>  Application-Ranks: guest[GUEST:guest],squire,knight,baron,builder,head_builder,senior_builder

Here you can specify:
- Whether to use SQL databases or local files for application storage
- Specify SQL database connection parameters
- Whether to use plot locations (PlotSquared dependency) or player locations
- Specify custom criteria for applications (Requires SQL database use)
- Whether to use server ranks or specify specific ranks for application availability
- Specify ranks that can be applied for via an application (must be a valid rank on the server)
---