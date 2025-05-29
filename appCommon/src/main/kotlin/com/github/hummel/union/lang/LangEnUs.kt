package com.github.hummel.union.lang

object LangEnUs : Lang {
	override fun getJson(): String = """
		{
			"birthday": "<@%d>: %s",
			"manager": "<@&%d>",
			"muted_channel": "<#%d>",
			"secret_channel": "<#%d>",
			
			"added_birthday": "Birthday has been added: <@%d>, %s.",
			"added_manager": "Manager role has been added: <@&%d>.",
			"added_muted_channel": "Muted channel has been added: <#%d>.",
			"added_secret_channel": "Secret channel has been added: <#%d>.",
			
			"removed_birthday": "Birthday has been removed: <@%d>.",
			"removed_manager": "Manager role has been removed: <@&%d>.",
			"removed_muted_channel": "Muted channel has been removed: <#%d>.",
			"removed_secret_channel": "Secret channel has been removed: <#%d>.",
			
			"cleared_birthdays": "Server birthdays have been cleared.",
			"cleared_managers": "Server manager roles have been cleared.",
			"cleared_muted_channels": "Server muted channels have been cleared.",
			"cleared_secret_channels": "Server secret channels have been cleared.",
			
			"has_birthdays": "Birthdays:",
			"has_managers": "Manager roles:",
			"has_muted_channels": "Muted channels:",
			"has_secret_channels": "Secret channels:",
			
			"no_birthdays": "There are no birthdays.",
			"no_managers": "There are no manager roles.",
			"no_muted_channels": "There are no muted channels.",
			"no_secret_channels": "There are no secret channels.",
			
			"set_chance_ai": "AI-Message chance has been set: %s.",
			"set_chance_emoji": "Emoji chance has been set: %s.",
			"set_chance_message": "Message chance has been set: %s.",
			"set_language": "Language has been set: %s.",
			"set_preprompt": "Preprompt has been set: %s",
			"set_name": "Name has been set: %s.",
			
			"current_chance_ai": "Current AI-message chance: %d.",
			"current_chance_emoji": "Current emoji chance: %d.",
			"current_chance_message": "Current message chance: %d.",
			"current_language": "Current language: %s.",
			"current_preprompt": "Current preprompt: %s",
			"current_name": "Current name: %s.",
			
			"cleared_data": "Server data has been cleared.",
			"cleared_bank": "Server speechbank have been cleared.",
			
			"exit": "Application will be turned off.",
			"import": "Data has been imported.",
			
			"msg_access": "Access",
			"msg_error": "Error",
			"msg_success": "Success",
			
			"no_access": "You cannot use this command.",
			
			"invalid_arg": "Invalid arguments.",
			"invalid_format": "Invalid arguments format.",
			
			"happy_birthday": "<@%d>, happy birthday to you!",
			
			"reset_preprompt": "Preprompt has been reset.",
			"reset_name": "Name has been reset.",
			"cleared_context": "Context cleared.",
			
			"january": "January %d",
			"february": "February %d",
			"march": "March %d",
			"april": "April %d",
			"may": "May %d",
			"june": "June %d",
			"july": "July %d",
			"august": "August %d",
			"september": "September %d",
			"october": "October %d",
			"november": "November %d",
			"december": "December %d",
			
			"long_message": "The answer is too long and exceeds the Discord limit. Try editing the prompt to say «Write 3 paragraphs».",
			
			"site_error": "Error: %s",

			"be": "belarusian",
			"en": "english",
			"ru": "russian",
			"uk": "ukrainian"
		}
	""".trimIndent()
}