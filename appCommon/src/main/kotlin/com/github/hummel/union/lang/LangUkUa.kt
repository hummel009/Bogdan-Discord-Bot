package com.github.hummel.union.lang

object LangUkUa : Lang {
	override fun getJson(): String = """
		{
			"birthday": "<@%d>: %s",
			"manager": "<@&%d>",
			"muted_channel": "<#%d>",
			"secret_channel": "<#%d>",
			
			"added_birthday": "Додано день народження: <@%d>, %s.",
			"added_manager": "Додано керівницьку роль: <@&%d>.",
			"added_muted_channel": "Додано відключений канал: <#%d>.",
			"added_secret_channel": "Додано секретний канал: <#%d>.",
			
			"removed_birthday": "Видалено день народження: <@%d>.",
			"removed_manager": "Видалено керівницьку роль: <@&%d>.",
			"removed_muted_channel": "Видалено відключений канал: <#%d>.",
			"removed_secret_channel": "Видалено секретний канал: <#%d>.",
			
			"cleared_birthdays": "Дні народження сервера очищено.",
			"cleared_managers": "Керівницькі ролі сервера очищено.",
			"cleared_muted_channels": "Відключені канали сервера очищено.",
			"cleared_secret_channels": "Секретні канали сервера очищено.",
			
			"has_birthdays": "Дні народження:",
			"has_managers": "Керівницькі ролі:",
			"has_muted_channels": "Заборонені канали:",
			"has_secret_channels": "Секретні канали:",
			
			"no_birthdays": "Днів народження немає.",
			"no_managers": "Керівницьких ролей немає.",
			"no_muted_channels": "Відключених каналів немає.",
			"no_secret_channels": "Секретних каналів немає.",
			
			"set_chance_ai": "Встановлено шанс ШІ-повідомлення: %d.",
			"set_chance_emoji": "Встановлено шанс емодзі: %d.",
			"set_chance_message": "Встановлено шанс повідомлення: %d.",
			"set_language": "Встановлено мову: %s.",
			"set_preprompt": "Встановлено препромпт: %s",
			"set_name": "Встановлено ім’я: %s.",
			
			"current_chance_ai": "Поточний шанс ШІ-повідомлення: %d.",
			"current_chance_emoji": "Поточний шанс емодзі: %d.",
			"current_chance_message": "Поточний шанс повідомлення: %d.",
			"current_language": "Поточна мова інтерфейсу: %s.",
			"current_preprompt": "Поточний препромпт: %s",
			"current_name": "Поточне ім’я: %s.",
			
			"cleared_data": "Дані сервера очищено.",
			"cleared_bank": "Спічбанк сервера очищено.",
			
			"exit": "Застосунок буде вимкнено.",
			"import": "Дані імпортовані.",
			
			"msg_access": "Доступ",
			"msg_error": "Помилка",
			"msg_success": "Успіх",
			
			"no_access": "Ви не можете використовувати цю команду.",
			
			"invalid_arg": "Неприпустимі аргументи.",
			"invalid_format": "Неприпустимий формат аргументів.",
			
			"happy_birthday": "<@%d>, з днем народження!",
			
			"reset_preprompt": "Препромпт відновлено.",
			"reset_name": "Ім’я відновлено.",
			"cleared_context": "Контекст очищено.",
			
			"january": "%d січня",
			"february": "%d лютого",
			"march": "%d березня",
			"april": "%d квітня",
			"may": "%d травня",
			"june": "%d червня",
			"july": "%d липня",
			"august": "%d серпня",
			"september": "%d вересня",
			"october": "%d жовтня",
			"november": "%d листопада",
			"december": "%d грудня",
			
			"long_message": "Відповідь надто довга та перевищує ліміт Discord. Поспробуйте відредагувати промпт на зразок «Напиші 3 абзаци».",
			
			"site_error": "Код помилки: %d [%s]",
			
			"be": "білоруська",
			"en": "англійська",
			"ru": "російська",
			"uk": "українська"
		}
	""".trimIndent()
}