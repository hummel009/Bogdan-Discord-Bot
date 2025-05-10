package com.github.hummel.union.lang

object LangBeBy : Lang {
	override fun getJson(): String = """
		{
			"birthday": "<@%d>: %s",
			"manager": "<@&%d>",
			"muted_channel": "<#%d>",
			"secret_channel": "<#%d>",
			
			"added_birthday": "Дададзены дзень нараджэння: <@%d>, %s.",
			"added_manager": "Дададзена кіраўніцкая роль: <@&%d>.",
			"added_muted_channel": "Дададзены адключаны канал: <#%d>.",
			"added_secret_channel": "Дададзены сакрэтны канал: <#%d>.",
			
			"removed_birthday": "Выдалены дзень нараджэння: <@%d>.",
			"removed_manager": "Выдалена кіраўніцкая роль: <@&%d>.",
			"removed_muted_channel": "Выдалены адключаны канал: <#%d>.",
			"removed_secret_channel": "Выдалены сакрэтны канал: <#%d>.",
			
			"cleared_birthdays": "Дні нараджэння сервера ачышчаны.",
			"cleared_managers": "Кіраўніцкія ролі сервера ачышчаны.",
			"cleared_muted_channels": "Адключаныя каналы сервера ачышчаны.",
			"cleared_secret_channels": "Сакрэтныя каналы сервера ачышчаны.",
			
			"has_birthdays": "Дні нараджэння:",
			"has_managers": "Кіраўніцкія ролі:",
			"has_muted_channels": "Забароненыя каналы:",
			"has_secret_channels": "Сакрэтныя каналы:",
			
			"no_birthdays": "Днёў нараджэння няма.",
			"no_managers": "Кіраўніцкіх роляў няма.",
			"no_muted_channels": "Адключаных каналаў няма.",
			"no_secret_channels": "Сакрэтных каналаў няма.",
			
			"set_chance_ai": "Усталяваны шанс ШІ-паведамлення: %d.",
			"set_chance_emoji": "Усталяваны шанс эмадзі: %d.",
			"set_chance_message": "Усталяваны шанс паведамлення: %d.",
			"set_language": "Усталявана мова: %s.",
			"set_preprompt": "Усталяваны прапромпт: %s",
			"set_name": "Усталявана імя: %s.",
			
			"current_chance_ai": "Бягучы шанс ШІ-паведамлення: %d.",
			"current_chance_emoji": "Бягучы шанс эмадзі: %d.",
			"current_chance_message": "Бягучы шанс паведамлення: %d.",
			"current_language": "Бягучая мова інтэрфейса: %s.",
			"current_preprompt": "Бягучы прапромпт: %s",
			"current_name": "Бягучае імя: %s.",
			
			"cleared_data": "Даныя сервера ачышчаны.",
			"cleared_bank": "Спічбанк сервера ачышчаны.",
			
			"exit": "Прыкладанне будзе выключана.",
			"import": "Даныя імпартаваны.",
			
			"msg_access": "Доступ",
			"msg_error": "Памылка",
			"msg_success": "Поспех",
			
			"no_access": "Вы не можаце выкарыстоўваць гэтую каманду.",
			
			"invalid_arg": "Недапушчальныя аргументы.",
			"invalid_format": "Недапушчальны фармат аргументаў.",
			
			"happy_birthday": "<@%d>, з днём нараджэння!",
			
			"reset_preprompt": "Прапромпт адноўлены.",
			"reset_name": "Імя адноўлена.",
			"cleared_context": "Кантэкст ачышчаны.",
			
			"january": "%d студзеня",
			"february": "%d лютага",
			"march": "%d сакавіка",
			"april": "%d красавіка",
			"may": "%d траўня",
			"june": "%d чэрвеня",
			"july": "%d ліпеня",
			"august": "%d жніўня",
			"september": "%d верасня",
			"october": "%d кастрычніка",
			"november": "%d лістапада",
			"december": "%d снежня",
			
			"long_message": "Адказ занадта доўгі і перавышае мяжу Discord. Паспрабуйце адрэгадаваць промпт на кшталт «Напішы 3 абзацы».",
			
			"site_error": "Код памылкі: %d [%s]",

			"be": "беларуская",
			"en": "англійская",
			"ru": "руская",
			"uk": "украінская"
		}
	""".trimIndent()
}