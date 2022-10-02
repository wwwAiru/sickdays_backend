package ru.egartech.sickday.mapper;

import lombok.NonNull;

import java.util.List;

public interface DtoMapper<IN, OUT> {

    OUT toDto(@NonNull IN in);

    List<OUT> toListDto(@NonNull List<IN> inList);

}
