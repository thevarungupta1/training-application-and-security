# What is FastAPI?
- Fast API is a modern, high performance Python web framework used to build REST APIs and microservices quickly and efficently

- it is designed for speed, developer productivity and correctness and is built on top of:
- Starlette - ASGI framework (high performance, async support)
- Pydantic - Data validation using Python type hints

Fast API is especially popular for cloud-native, microservices and AI/ML backend




# Why FAST API was created
Traditional python framework (like flask, Django REST)
 - require manual validation
 - have slower performance for async workload
 - need extra libraries for docs, typing and validation

 FastAPI solves this by using:
 - Python type hints as the single source of truth
 - asycn first design
 - automatatic OpenAPI documentation



 # Important features
    - High performance: comparable to Node.js and Go
    - Fast to code: increases developer productivity
    - Automatic interactive API docs: Swagger UI and ReDoc
    - Data validation: using Pydantic models
    - Dependency injection system
    - Asynchronous support: built-in async/await support
    - Security and authentication: OAuth2, JWT, etc.
    - Easy testing: built-in support for testing with pytest
    - Extensible: middleware, custom routes, etc.






# Exercise : Create a REST API using FastAPI

### Objective
- Build a CRUD REST API for users
- Validation
- Swagger Docs
- Proper project structure
- Error Handling



### Step 1: Create a virtual envirnment
```bash
mkdir fastapi-rest-api
cd fastapi-rest-api
python -m venv venv
venv\Scripts\activate
```

### Step 2: Install FastAPI and Uvicorn
```bash
pip install fastapi uvicorn
pip freeze
```

### Step 3: create basic project structure

```yaml
fastapi-rest-api/
├── app/
│   ├── main.py
│   ├── models.py
│   ├── schemas.py
│   ├── routes.py
│   └── database.py
├── requirements.txt
└── README.md
|--venv/
```

save dependenices
```bash
pip freeze > requirements.txt
```

### Step 4: Create First FastAPI App
`app/main.py`

```python
from fastapi import FastAPI

app = FastAPI(
    ttile = "User Management API",
    description="API for managing user accounts and profiles",
    version="1.0.0"
)

@app.get("/")
def health_check():
    return { "status": "API is running" }

```

### Step 5: Run the app
```bash
uvicorn app.main:app --reload
```

Open Browser
- API: `http://127.0.0.1:8000`
- Swagger: `http://127.0.0.1:8000/docs`
- ReDoc: `http://127.0.0.1:8000/redoc`


### Step 6: Create Data Model (Schema)
FastAPI uses Pydantic for validation

`app/schemas.py`
```
from pydantic import BaseModel, EmailStr
from typing import Optional

class UserCreate(BaseModel):
    name: str
    email: EmailStr
    age: int

class UserResponse(UserCreate):
    id: int
```


### Step 7: Create Fake Database (In-Memory)
`app/database.py`

```
users_db = []
```


### Step 8: Create API Routes (CRUD)
`api/routes.py`

```python
from fastapi import APIRouter, HTTPException
from typing import List
from app.schemas import UserCreate, UserResponse
from app.database import users_db

router = APIRouter(prefix="/users", tags=["users"])

@router.post("/", response_model=UserResponse)
def create_user(user: UserCreate):
    user_id = len(users_db) + 1
    user_data = user.dict()
    user_data["id"] = user_id
    users_db.append(user_data)
    return user_data

@router.get("/", response_model=List[UserResponse])
def get_users():
    return users_db

@router.get("/{user_id}", response_model=UserResponse)
def get_user(user_id: int):
    for user in users_db:
        if user["id"] == user_id:
            return user
    raise HTTPException(status_code=404, detail="User not found")

@router.delete("/{user_id}")
def delete_user(user_id: int):
    for index, user in enumerate(users_db):
        if user["id"] == user_id:
            del users_db[index]
            return {"detail": "User deleted"}
    raise HTTPException(status_code=404, detail="User not found")

```

### Step 9: Register routes in Main App
Update `app/main.py`

```python
from fastapi import FastAPI
from app.routes import router as user_router

app = FastAPI(
    title = "User Management API",
    description="API for managing user accounts and profiles",
    version="1.0.0"
)

app.include_router(user_router)


@app.get("/")
def health_check():
    return { "status": "API is running" }
```


# Step 10: Install MySQl Dependencies
```bash
pip install sqlalchemy pymysql python-dotenv
pip freeze > requirements.txt
```