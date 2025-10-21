# XandonePrimalCalendar

A comprehensive custom calendar system for Minecraft Paper 1.21.1 servers.

## Features

- **Custom Calendar System** - Create your own months, days, years, and eras
- **Holidays & Events** - Schedule recurring or one-time holidays/events
- **Full Config Customization** - YAML configuration for all calendar settings
- **Custom Year Lengths** - Make your year any length you want
- **Historical Calendar Records** - Record major events for future reference
- **Custom Date Formats** - Choose how dates display
- **Admin Commands** - Comprehensive admin command system
- **Permissions** - Full permission support
- **Offline Scheduling** - Events run even when no players are online
- **Discord Integration** - Announce events to Discord via webhooks
- **Developer API** - Let other plugins hook into the calendar
- **Language Support** - Multi-language support
- **Holiday Command Triggers** - Holidays can execute commands
- **Birthday Tracker** - Players can set birthdays for rewards
- **Vault Support** - Economy integration
- **PlaceholderAPI Support** - Use calendar data in other plugins
- **Real-Time Sync** - Option to follow real-world date/time
- **Server-Time Calendar** - Runs on in-game days

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/XandonePrimalCalendar/config.yml`

## Commands

### Player Commands
- `/calendar` - View the current calendar date
- `/holiday <name> <day> <month>` - Create a personal holiday (requires holiday coin)
- `/birthday [set <day> <month>]` - Set or view your birthday

### Admin Commands
- `/caladmin setdate <day> <month> <year>` - Set the calendar date
- `/caladmin advance <days>` - Advance the calendar by X days
- `/caladmin reload` - Reload the plugin configuration
- `/caladmin holtoken give <player> <amount>` - Give holiday coins
- `/caladmin addholiday <name> <day> <month>` - Add a recurring holiday
- `/caladmin removeholiday <name>` - Remove a holiday
- `/caladmin addevent <name> <day> <month> [year]` - Add an event
- `/caladmin removeevent <name>` - Remove an event
- `/caladmin listholidays` - List all holidays
- `/caladmin listevents` - List all events
- `/caladmin record <title> <description>` - Add historical record
- `/caladmin history` - View historical records

## Permissions

- `primalcalendar.admin` - Access to all admin commands
- `primalcalendar.calendar.view` - View the calendar
- `primalcalendar.holiday.create` - Create personal holidays
- `primalcalendar.birthday` - Set and view birthdays
- `primalcalendar.reload` - Reload the plugin

## Configuration

See `config.yml` for full configuration options including:
- Month names and lengths
- Date formats
- Era names
- Discord webhook settings
- Birthday rewards
- Calendar mode (real-time vs server-time)

## API Usage

See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for developer API documentation.

## Support

For issues or questions, please open an issue on the GitHub repository.

## License

This plugin is provided as-is for use on Minecraft servers.
