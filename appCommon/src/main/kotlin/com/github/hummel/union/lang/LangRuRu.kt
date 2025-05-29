package com.github.hummel.union.lang

object LangRuRu : Lang {
	override fun getJson(): String = """
		{
			"birthday": "<@%d>: %s",
			"manager": "<@&%d>",
			"muted_channel": "<#%d>",
			"secret_channel": "<#%d>",
			
			"added_birthday": "Добавлен день рождения: <@%d>, %s.",
			"added_manager": "Добавлена управляющая роль: <@&%d>.",
			"added_muted_channel": "Добавлен отключенный канал: <#%d>.",
			"added_secret_channel": "Добавлен секретный канал: <#%d>.",
			
			"removed_birthday": "Удалён день рождения: <@%d>.",
			"removed_manager": "Удалена управляющая роль: <@&%d>.",
			"removed_muted_channel": "Удалён отключенный канал: <#%d>.",
			"removed_secret_channel": "Удалён секретный канал: <#%d>.",
			
			"cleared_birthdays": "Дни рождения сервера очищены.",
			"cleared_managers": "Управляющие роли сервера очищены.",
			"cleared_muted_channels": "Отключенные каналы сервера очищены.",
			"cleared_secret_channels": "Секретные каналы сервера очищены.",
			
			"has_birthdays": "Дни рождения:",
			"has_managers": "Управляющие роли:",
			"has_muted_channels": "Запрещённые каналы:",
			"has_secret_channels": "Секретные каналы:",
			
			"no_birthdays": "Дней рождения нет.",
			"no_managers": "Управляющих ролей нет.",
			"no_muted_channels": "Отключенных каналов нет.",
			"no_secret_channels": "Секретных каналов нет.",
			
			"set_chance_ai": "Установлен шанс ИИ-сообщения: %d.",
			"set_chance_emoji": "Установлен шанс эмодзи: %d.",
			"set_chance_message": "Установлен шанс сообщения: %d.",
			"set_language": "Установлен язык: %s.",
			"set_preprompt": "Установлен препромпт: %s",
			"set_name": "Установлено имя: %s.",
			
			"current_chance_ai": "Текущий шанс ИИ-сообщения: %d.",
			"current_chance_emoji": "Текущий шанс эмодзи: %d.",
			"current_chance_message": "Текущий шанс сообщения: %d.",
			"current_language": "Текущий язык интерфейса: %s.",
			"current_preprompt": "Текущий препромпт: %s",
			"current_name": "Текущее имя: %s.",
			
			"cleared_data": "Данные сервера очищены.",
			"cleared_bank": "Спичбанк сервера очищены.",
			
			"exit": "Приложение будет выключено.",
			"import": "Данные импортированы.",
			
			"msg_access": "Доступ",
			"msg_error": "Ошибка",
			"msg_success": "Успех",
			
			"no_access": "Вы не можете использовать эту команду.",
			
			"invalid_arg": "Недопустимые аргументы.",
			"invalid_format": "Недопустимый формат аргументов.",
			
			"happy_birthday": "<@%d>, с днём рождения!",
			
			"reset_preprompt": "Препромпт восстановлен.",
			"reset_name": "Имя восстановлено.",
			"cleared_context": "Контекст очищен.",
			
			"january": "%d января",
			"february": "%d февраля",
			"march": "%d марта",
			"april": "%d апреля",
			"may": "%d мая",
			"june": "%d июня",
			"july": "%d июля",
			"august": "%d августа",
			"september": "%d сентября",
			"october": "%d октября",
			"november": "%d ноября",
			"december": "%d декабря",
			
			"long_message": "Ответ слишком длинный и превышает лимит Discord. Попробуйте отредактировать промпт на манер «Напиши 3 абзаца».",
			
			"site_error": "Ошибка: %s",

			"be": "белорусский",
			"en": "английский",
			"ru": "русский",
			"uk": "украинский"
		}
	""".trimIndent()
}