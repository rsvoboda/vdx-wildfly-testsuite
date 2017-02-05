// TODO Can be used after https://github.com/wildfly-extras/creaper/pull/152 is merged  and new Creaper released
// elytron."security-realms".appendNode { 'properties-realm'("name":"JBossWS", "plain-text":"true") }

root.profile.subsystem."security-realms".appendNode { 'properties-realm'("name":"JBossWS", "plain-text":"true") }