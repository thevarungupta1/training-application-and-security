# from fastapi import APIRouter, HTTPException
# from typing import List
# from app.schemas import UserCreate, UserResponse
# from app.database import users_db

# router = APIRouter(prefix="/users", tags=["users"])

# @router.post("/", response_model=UserResponse)
# def create_user(user: UserCreate):
#     user_id = len(users_db) + 1
#     user_data = user.dict()
#     user_data["id"] = user_id
#     users_db.append(user_data)
#     return user_data

# @router.get("/", response_model=List[UserResponse])
# def list_users():
#     return users_db


# @router.get("/{user_id}", response_model=UserResponse)
# def get_user(user_id: int):
#     for user in users_db:
#         if user["id"] == user_id:
#             return user
#     raise HTTPException(status_code=404, detail="User not found")

# @router.put("/{user_id}", response_model=UserResponse)
# def update_user(user_id: int, user: UserCreate):
#     for index, existing_user in enumerate(users_db):
#         if existing_user["id"] == user_id:
#             updated_user = user.dict()
#             updated_user["id"] = user_id
#             users_db[index] = updated_user
#             return updated_user
#     raise HTTPException(status_code=404, detail="User not found")

# @router.delete("/{user_id}", response_model=UserResponse)
# def delete_user(user_id: int):
#     for index, user in enumerate(users_db):
#         if user["id"] == user_id:
#             deleted_user = users_db.pop(index)
#             return deleted_user
#     raise HTTPException(status_code=404, detail="User not found")


from fastapi import APIRouter, HTTPException, Depends
from typing import List
from app import crud, schemas
from app.database import get_db
from sqlalchemy.orm import Session

router = APIRouter(prefix="/users", tags=["users"])

# create user
@router.post("/", response_model=schemas.UserResponse)
def create_user(user: schemas.UserCreate, db: Session = Depends(get_db)):
    return crud.create_user(db=db, user=user)

# get all users
@router.get("/", response_model=List[schemas.UserResponse])
def list_users(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    return crud.get_users(db=db, skip=skip, limit=limit)

# get user by id
@router.get("/{user_id}", response_model=schemas.UserResponse)
def get_user(user_id: int, db: Session = Depends(get_db)):
    user = crud.get_user(db=db, user_id=user_id)
    if not user:
        raise HTTPException(status_code=404, detail="User not found")
    return user

# update user by id
@router.put("/{user_id}", response_model=schemas.UserResponse)
def update_user(user_id: int, user: schemas.UserUpdate, db: Session = Depends(get_db)):
    updated_user = crud.update_user(db=db, user_id=user_id, user=user)
    if not updated_user:
        raise HTTPException(status_code=404, detail="User not found")
    return updated_user

# delete user by id
@router.delete("/{user_id}", response_model=schemas.UserResponse)
def delete_user(user_id: int, db: Session = Depends(get_db)):
    deleted_user = crud.delete_user(db=db, user_id=user_id)
    if not deleted_user:
        raise HTTPException(status_code=404, detail="User not found")
    return deleted_user