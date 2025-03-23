# DeepSeek AI Chat Application

A modern Android chat application that allows users to have conversations with an AI assistant powered by DeepSeek. The app features a clean interface, chat history management, and user authentication.

## Features

- User Authentication (Login/Signup)
- Real-time AI Chat Conversations
- Chat History Management
- Navigation Drawer with Chat Sessions
- Dark/Light Theme Support

## App Flow

1. **Authentication Flow**
   - App starts with checking for existing JWT token
   - If no token exists, redirects to Login screen
   - Users can either login or navigate to signup
   - After successful auth, redirects to main chat

2. **Chat Flow**
   - Main screen shows chat interface
   - Users can send messages and receive AI responses
   - Typing indicators show when AI is responding
   - Chat history is maintained per session
   - Users can start new chats or view previous ones

3. **Session Management**
   - Each chat conversation is a session
   - Sessions are saved and can be accessed later
   - Session titles are auto-generated based on content
   - Users can switch between different chat sessions

## Key Files and Their Functions

### Activities
- `MainActivity.java`: Main chat interface and navigation drawer management
- `LoginActivity.java`: Handles user login
- `SignUpActivity.java`: Handles new user registration

### Models
- `ChatMessage.java`: Represents individual chat messages
- `ChatSession.java`: Represents a chat conversation session
- `ChatMessageResponse.java`: AI response model
- `ChatSessionStartResponse.java`: New session response
- `LoginResponse.java`: Authentication response
- `SendChatMessageRequest.java`: Message request model
- `CloseChatSessionRequest.java`: Session closing model

### API
- `ApiServices.java`: Defines all API endpoints
- `RetrofitClient.java`: API client configuration
- `TokenManager.java`: JWT token management

### Layouts
- `activity_main.xml`: Main chat interface layout
- `activity_login.xml`: Login screen layout
- `activity_sign_up.xml`: Signup screen layout
- `nav_header.xml`: Navigation drawer header
- `user_message_item.xml`: User message bubble layout
- `bot_message_item.xml`: Bot message bubble layout
- `typing_indicator.xml`: Loading animation layout

### Adapters
- `ChatMessageAdapter.java`: Handles chat message display

### Utils
- `TokenManager.java`: JWT token interceptor for API calls

## Unused Files

1. `dark_input_background.xml` - Replaced by message_input_background.xml
2. `dark_button_background.xml` - Replaced by send_button_background.xml
3. `typing_dot_1.xml`, `typing_dot_2.xml`, `typing_dot_3.xml` - Can be removed if not using dot animation
4. Any color resources prefixed with "dark_" in colors.xml

## API Endpoints Used

- POST `/api/v1/user/signup`: User registration
- POST `/api/v1/user/signin`: User login
- POST `/api/v1/chat/start`: Start new chat session
- POST `/api/v1/chat/send`: Send message to AI
- GET `/api/v1/chat/sessions`: Get chat history
- POST `/api/v1/chat/close`: Close chat session
- GET `/api/v1/chat/history/{chatSessionId}`: Get session messages 