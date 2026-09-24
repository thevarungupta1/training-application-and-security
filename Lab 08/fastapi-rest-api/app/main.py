from fastapi import FastAPI
from app.routes import router as users_router

app = FastAPI(
    title="My FastAPI Application",
    description="This is a sample FastAPI application",
    version="1.0.0"
)

app.include_router(users_router)

@app.get("/")
def health_check():
    return {"status": "API is running"}